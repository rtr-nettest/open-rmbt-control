CREATE TABLE public.qoe_classification (
                                           uid serial4 NOT NULL,
                                           category varchar(30) NOT NULL,
                                           dl_4 float8 NOT NULL,
                                           dl_3 float8 NOT NULL,
                                           dl_2 float8 NOT NULL,
                                           ul_4 float8 NOT NULL,
                                           ul_3 float8 NOT NULL,
                                           ul_2 float8 NOT NULL,
                                           ping_4 float8 NOT NULL,
                                           ping_3 float8 NOT NULL,
                                           ping_2 float8 NOT NULL,
                                           CONSTRAINT qoe_classification_pk PRIMARY KEY (uid)
);

INSERT INTO public.qoe_classification (category,dl_4,dl_3,dl_2,ul_4,ul_3,ul_2,ping_4,ping_3,ping_2) VALUES
	 ('video_conferencing',20000.0,6000.0,3000.0,20000.0,6000.0,3000.0,25000000,50000000,100000000),
	 ('video_hd',10000.0,4000.0,2000.0,1000.0,400.0,200.0,50000000,100000000,250000000),
	 ('gaming',8000.0,4000.0,2000.0,8000.0,4000.0,2000.0,10000000,10000001,50000000);
