create database capstone_db;
SET time_zone = '+9:00';


use capstone_db;
drop table search_noun;
create table search_noun(
	ticker	varchar(6)	NOT NULL,
	noun	varchar(15)	NOT NULL,
	count	int(6)	NOT NULL,
	company_name	varchar(20)	NOT NULL,
    PRIMARY KEY (noun , ticker)
);
ALTER TABLE search_noun ADD INDEX search_noun_id(noun);
drop table news;
create table news(
	ticker varchar(6) NOT NULL,
	provider	varchar(10)	NOT NULL,
	date	datetime	NOT NULL,
    rink varchar(120) not null,
	title	varchar(50)	NOT NULL,
	description	varchar(5000) NOT NULL,
	sentiment	varchar(2) NOT NULL,

    PRIMARY KEY (ticker , date)
);

drop table news2;
create table news2(
	ticker varchar(6) NOT NULL,
	provider	varchar(10)	NOT NULL,
	date	datetime	NOT NULL,
    rink varchar(120) not null,
	title	varchar(50)	NOT NULL,
	description	varchar(5000) NOT NULL,
	sentiment	varchar(2) NOT NULL,
    PRIMARY KEY (ticker , date)
);


drop table best_rise_rate_keyword;
create table best_rise_rate_keyword(
	noun varchar(15) NOT NULL,
    change_rate float not null,
    PRIMARY KEY (noun)
);
drop table stock_price;
create table stock_price(
	ticker varchar(15) NOT NULL,
	date	datetime	NOT NULL,
    end_price int not null,
    rate float not null,
    company_name varchar(15) not null,
    PRIMARY KEY (ticker,date)
);
drop table now_stock_price;
create table now_stock_price(
	ticker varchar(15) NOT NULL,
	start_price	int	NOT NULL,
	high_price	int	NOT NULL,
	low_price	int	NOT NULL,
    end_price int not null,
	volume int not null,
    rate float not null,
    PRIMARY KEY (ticker)
);


drop table sector;
create table sector(
	company_name varchar(15) NOT NULL,
	keyword varchar(15) NOT NULL,
    ticker varchar(6) not null,
    PRIMARY KEY (ticker,keyword)
);
drop table thema;
create table thema(
	company_name varchar(15) NOT NULL,
	keyword varchar(30) NOT NULL,
	ticker varchar(6) not null,
    PRIMARY KEY (ticker,keyword)
);
drop table time_table;
create table time_table(
	day_minute1 datetime,
	day_minute10 datetime,
    day_date datetime,
    day_week datetime
);


drop table company_info_table;
create table company_info_table(
	ticker varchar(6),
    company_name varchar(15),
    company_info varchar(400),
    market_cap int,
    per float,
    eps int,
    est_per float,
    est_eps int,
    pbr float,
    dvr float,
    primary key(ticker)
);

drop table terms_table;
create table terms_table(
	terms varchar(10),
	meaning varchar(30),
	primary key(terms)
);
drop table chart_trend;
create table chart_trend(
	end_price int not null,
	date	datetime	NOT NULL,
	period	int not null,
    label int not null
);


drop table trend_stock;
create table trend_stock(
	ticker	varchar(6)	NOT NULL,
	company_name	varchar(20)	NOT NULL,
	period	int not null,
	label int not null,
	ranking int not null
);
