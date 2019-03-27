CREATE TABLE
    rpt_template
    (
        url VARCHAR(64) NOT NULL,
        file_name VARCHAR(128),
        excel_template BLOB,
        PRIMARY KEY (url)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;