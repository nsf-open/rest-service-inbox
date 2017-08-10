CREATE SCHEMA IF NOT EXISTS dbo;
SET SCHEMA dbo;

CREATE TABLE
    inbx_msg
(
    inbx_msg_id NUMERIC(10, 0) IDENTITY,
    user_lan_id CHAR(8)       NOT NULL,
    smry        VARCHAR(1500) NOT NULL,
    cre_date    DATETIME      NOT NULL,
    prty        NUMERIC(2, 0) NOT NULL,
    type        CHAR(1)       NOT NULL,
    actn_link   VARCHAR(1000) NULL,
    actn_lbl   VARCHAR(1000) NULL,
    innl        BIT           NOT NULL,
    exp_date DATETIME      NULL,
    last_updt_user    varchar(1000) NOT NULL,
    last_updt_pgm     varchar(1000) NOT NULL,
    last_updt_tmsp    varchar(1000) NOT NULL
);



 



