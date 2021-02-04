ALTER TABLE public.news
    ADD COLUMN start_time timestamp with time zone NOT NULL DEFAULT NOW();
ALTER TABLE public.news
    ADD COLUMN end_time timestamp with time zone;

CREATE OR REPLACE FUNCTION public.getNewsStatus(boolean, timestamp with time zone, timestamp with time zone) RETURNS character varying
    LANGUAGE plpgsql
AS
$_$
DECLARE
    active alias for $1;
    startDate alias for $2;
    endDate alias for $3;
    now       timestamp with time zone;
    isEnded   boolean;
    isStarted boolean;
BEGIN
    now := NOW();
    isEnded := endDate IS NOT NULL AND endDate < now;
    isStarted := startDate < now;

    RETURN CASE
               WHEN active AND NOT isEnded AND NOT isStarted
                   THEN 'SCHEDULED'
               WHEN active AND NOT isEnded AND isStarted
                   THEN 'PUBLISHED'
               WHEN active AND isEnded
                   THEN 'EXPIRED'
               ELSE 'DRAFT'
        END;
END;
$_$;

CREATE OR REPLACE VIEW news_view AS
SELECT n.uid,
       n.title_en,
       n.title_de,
       n.text_en,
       n.text_de,
       n.plattform,
       n.active,
       n.force,
       n.max_software_version_code,
       n.min_software_version_code,
       n.uuid,
       n.start_time,
       n.end_time,
       n.time,
       getNewsStatus(n.active, n.start_time, n.end_time)
           AS status
FROM public.news n;