CREATE TABLE public.test_server_types(
    test_server_uid bigint not null,
    server_type varchar(60) NOT NULL
);

INSERT INTO public.test_server_types (test_server_uid, server_type)
SELECT DISTINCT ts.uid , ts.server_type FROM public.test_server ts;

ALTER TABLE public.test_server
    ADD COLUMN IF not exists archived bool NOT NULL DEFAULT false;

ALTER TABLE public.test_server
    ADD COLUMN IF not exists encrypted bool NOT NULL DEFAULT false;

UPDATE public.test_server
SET encrypted = true
WHERE server_type <> 'HW_PROBE';
