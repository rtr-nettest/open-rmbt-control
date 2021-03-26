ALTER TABLE public.test
    ADD COLUMN IF not exists measurement_type_flag varchar;
