package at.rtr.rmbt.filter;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Api logging filter class.
 */
public class ApiLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private final String requestIdParamName;

    /**
     * Creates a new ApiLoggingFilter instance.
     *
     * @param requestIdParamName the Request id param name
     */
    public ApiLoggingFilter(String requestIdParamName) {
        this.requestIdParamName = requestIdParamName;
    }

    /**
     * Do filter.
     *
     * @param request the Request
     * @param response the Response
     * @param chain the Chain
     * @throws IOException if an error occurs
     * @throws ServletException if an error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            Map<String, String> requestMap = this.getTypesafeRequestMap(httpServletRequest);
            BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpServletRequest);
            BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpServletResponse);
            String requestId = requestMap.containsKey(requestIdParamName) ? requestMap.get(requestIdParamName)
                    : UUID.randomUUID().toString();
            MDC.put("REQUEST_ID", requestId);
            final StringBuilder logRequest = new StringBuilder("HTTP ").append(httpServletRequest.getMethod())
                    .append(" \"").append(httpServletRequest.getServletPath()).append("\" ").append(", parameters=")
                    .append(requestMap).append(", body=").append(bufferedRequest.getRequestBody())
                    .append(", headers={").append(Collections.list(((HttpServletRequest) request)
                                    .getHeaderNames()).stream()
                            .map(r -> String.format("\"%s\": \"%s\"", r, ((HttpServletRequest) request).getHeader(r)))
                            .collect(Collectors.joining(", ")) + "}");
            LOGGER.info(logRequest.toString());
            try {
                chain.doFilter(bufferedRequest, bufferedResponse);
            } catch (Throwable a) {
                if (isClientAbort(a)) {
                    LOGGER.info("User disconnected before the response was completed: {} {}",
                            httpServletRequest.getMethod(), httpServletRequest.getServletPath());
                } else {
                    LOGGER.error(a.getMessage(), a);
                }
            } finally {
                // Only log textual response bodies; binary payloads (e.g. application/pdf) would
                // otherwise dump raw bytes into the log.
                final String contentType = httpServletResponse.getContentType();
                if (isTextualContentType(contentType)) {
                    final String content = bufferedResponse.getContent();
                    LOGGER.info("HTTP RESPONSE " + content.substring(0, Math.min(content.length(), 10000)));
                } else {
                    LOGGER.info("HTTP RESPONSE [" + contentType + " body not logged]");
                }
                MDC.clear();
            }
        } catch (Throwable a) {
            LOGGER.error(a.getMessage(), a);
        }
    }

    /** True if the exception (or any cause) is a client-side disconnect (broken pipe / reset). */
    private static boolean isClientAbort(Throwable t) {
        for (Throwable c = t; c != null; c = c.getCause()) {
            if ("ClientAbortException".equals(c.getClass().getSimpleName())) {
                return true;
            }
            final String m = c.getMessage();
            if (c instanceof java.io.IOException && m != null
                    && (m.contains("Broken pipe") || m.contains("Connection reset"))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTextualContentType(String contentType) {
        if (contentType == null) {
            return true;
        }
        final String ct = contentType.toLowerCase();
        return ct.startsWith("text/") || ct.contains("json") || ct.contains("xml");
    }

    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        Map<String, String> typesafeRequestMap = new HashMap<String, String>();
        Enumeration<?> requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            String requestParamName = (String) requestParamNames.nextElement();
            String requestParamValue;
            if (requestParamName.equalsIgnoreCase("password")) {
                requestParamValue = "********";
            } else {
                requestParamValue = request.getParameter(requestParamName);
            }
            typesafeRequestMap.put(requestParamName, requestParamValue);
        }
        return typesafeRequestMap;
    }

    /**
     * Buffered request wrapper class.
     */
    private static final class BufferedRequestWrapper extends HttpServletRequestWrapper {
        private ByteArrayInputStream bais = null;
        private ByteArrayOutputStream baos = null;
        private BufferedServletInputStream bsis = null;
        private byte[] buffer = null;

        /**
         * Creates a new BufferedRequestWrapper instance.
         *
         * @param req the Req
         * @throws IOException if an error occurs
         */
        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);
            // Read InputStream and store its content in a buffer.
            InputStream is = req.getInputStream();
            this.baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int read;
            while ((read = is.read(buf)) > 0) {
                this.baos.write(buf, 0, read);
            }
            this.buffer = this.baos.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream() {
            this.bais = new ByteArrayInputStream(this.buffer);
            this.bsis = new BufferedServletInputStream(this.bais);
            return this.bsis;
        }

        String getRequestBody() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
            String line = null;
            StringBuilder inputBuffer = new StringBuilder();
            do {
                line = reader.readLine();
                if (null != line) {
                    inputBuffer.append(line.trim());
                }
            } while (line != null);
            reader.close();
            return inputBuffer.toString().trim();
        }
    }

    /**
     * Buffered servlet input stream class.
     */
    private static final class BufferedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream bais;

        /**
         * Creates a new BufferedServletInputStream instance.
         *
         * @param bais the Bais
         */
        public BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        /**
         * Available.
         *
         * @return the result
         */
        @Override
        public int available() {
            return this.bais.available();
        }

        /**
         * Read.
         *
         * @return the result
         */
        @Override
        public int read() {
            return this.bais.read();
        }

        /**
         * Read.
         *
         * @param buf the Buf
         * @param off the Off
         * @param len the Len
         * @return the result
         */
        @Override
        public int read(byte[] buf, int off, int len) {
            return this.bais.read(buf, off, len);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }

    /**
     * Tee servlet output stream class.
     */
    public class TeeServletOutputStream extends ServletOutputStream {
        private final TeeOutputStream targetStream;

        /**
         * Creates a new TeeServletOutputStream instance.
         *
         * @param one the One
         * @param two the Two
         */
        public TeeServletOutputStream(OutputStream one, OutputStream two) {
            targetStream = new TeeOutputStream(one, two);
        }

        /**
         * Write.
         *
         * @param arg0 the Arg 0
         * @throws IOException if an error occurs
         */
        @Override
        public void write(int arg0) throws IOException {
            this.targetStream.write(arg0);
        }

        /**
         * Flush.
         *
         * @throws IOException if an error occurs
         */
        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        /**
         * Close.
         *
         * @throws IOException if an error occurs
         */
        public void close() throws IOException {
            super.close();
            this.targetStream.close();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

    /**
     * Buffered response wrapper class.
     */
    public class BufferedResponseWrapper implements HttpServletResponse {
        HttpServletResponse original;
        TeeServletOutputStream tee;
        ByteArrayOutputStream bos;

        /**
         * Creates a new BufferedResponseWrapper instance.
         *
         * @param response the Response
         */
        public BufferedResponseWrapper(HttpServletResponse response) {
            original = response;
        }

        public String getContent() throws IOException {
            if (Objects.isNull(bos)) {
                this.getOutputStream();
            }
            return bos.toString();
        }

        public PrintWriter getWriter() throws IOException {
            return original.getWriter();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (tee == null) {
                bos = new ByteArrayOutputStream();
                tee = new TeeServletOutputStream(original.getOutputStream(), bos);
            }
            return tee;

        }

        @Override
        public String getCharacterEncoding() {
            return original.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return original.getContentType();
        }

        @Override
        public void setCharacterEncoding(String charset) {
            original.setCharacterEncoding(charset);
        }

        @Override
        public void setContentLength(int len) {
            original.setContentLength(len);
        }

        @Override
        public void setContentLengthLong(long l) {
            original.setContentLengthLong(l);
        }

        @Override
        public void setContentType(String type) {
            original.setContentType(type);
        }

        @Override
        public void setBufferSize(int size) {
            original.setBufferSize(size);
        }

        @Override
        public int getBufferSize() {
            return original.getBufferSize();
        }

        /**
         * Flush buffer.
         *
         * @throws IOException if an error occurs
         */
        @Override
        public void flushBuffer() throws IOException {
            tee.flush();
        }

        /**
         * Reset buffer.
         */
        @Override
        public void resetBuffer() {
            original.resetBuffer();
        }

        @Override
        public boolean isCommitted() {
            return original.isCommitted();
        }

        /**
         * Reset.
         */
        @Override
        public void reset() {
            original.reset();
        }

        @Override
        public void setLocale(Locale loc) {
            original.setLocale(loc);
        }

        @Override
        public Locale getLocale() {
            return original.getLocale();
        }

        /**
         * Add cookie.
         *
         * @param cookie the Cookie
         */
        @Override
        public void addCookie(Cookie cookie) {
            original.addCookie(cookie);
        }

        /**
         * Contains header.
         *
         * @param name the Name
         * @return the result
         */
        @Override
        public boolean containsHeader(String name) {
            return original.containsHeader(name);
        }

        /**
         * Encode URL.
         *
         * @param url the Url
         * @return the result
         */
        @Override
        public String encodeURL(String url) {
            return original.encodeURL(url);
        }

        /**
         * Encode redirect URL.
         *
         * @param url the Url
         * @return the result
         */
        @Override
        public String encodeRedirectURL(String url) {
            return original.encodeRedirectURL(url);
        }

     /*   @SuppressWarnings("deprecation")
        @Override
        public String encodeUrl(String url) {
            return original.encodeUrl(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeRedirectUrl(String url) {
            return original.encodeRedirectUrl(url);
        }*/

        @Override
        public void sendError(int sc, String msg) throws IOException {
            original.sendError(sc, msg);
        }

        /**
         * Send error.
         *
         * @param sc the Sc
         * @throws IOException if an error occurs
         */
        @Override
        public void sendError(int sc) throws IOException {
            original.sendError(sc);
        }

        /**
         * Send redirect.
         *
         * @param location the Location
         * @throws IOException if an error occurs
         */
        @Override
        public void sendRedirect(String location) throws IOException {
            original.sendRedirect(location);
        }

        @Override
        public void setDateHeader(String name, long date) {
            original.setDateHeader(name, date);
        }

        /**
         * Add date header.
         *
         * @param name the Name
         * @param date the Date
         */
        @Override
        public void addDateHeader(String name, long date) {
            original.addDateHeader(name, date);
        }

        @Override
        public void setHeader(String name, String value) {
            original.setHeader(name, value);
        }

        /**
         * Add header.
         *
         * @param name the Name
         * @param value the Value
         */
        @Override
        public void addHeader(String name, String value) {
            original.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            original.setIntHeader(name, value);
        }

        /**
         * Add int header.
         *
         * @param name the Name
         * @param value the Value
         */
        @Override
        public void addIntHeader(String name, int value) {
            original.addIntHeader(name, value);
        }

        @Override
        public void setStatus(int sc) {
            original.setStatus(sc);
        }

        @SuppressWarnings("deprecation")
      /*  @Override
        public void setStatus(int sc, String sm) {
            original.setStatus(sc, sm);
        }*/

        @Override
        public String getHeader(String arg0) {
            return original.getHeader(arg0);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return original.getHeaderNames();
        }

        @Override
        public Collection<String> getHeaders(String arg0) {
            return original.getHeaders(arg0);
        }

        @Override
        public int getStatus() {
            return original.getStatus();
        }
    }
}