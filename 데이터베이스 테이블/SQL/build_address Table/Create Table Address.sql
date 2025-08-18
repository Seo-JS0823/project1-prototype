create table road_code(
road_code varchar2(30),
road_name varchar2(160) not null,
town_code varchar2(3) not null,
province varchar2(80) not null,
district varchar2(80) null,
town varchar2(80) null,
primary key (road_code, town_code));

create table road_address(
address_code varchar2(50) primary key,
road_code varchar2(30),
town_code varchar(3),
building_num_main number(5,0) not null,
building_num_sub number(5,0) not null,
foreign key (road_code, town_code)
references road_code (road_code, town_code));

create table road_SubData(
address_code varchar2(50),
zipcode varchar2(5) not null,
building_name varchar2(80) null,
official_building_name varchar2(80) null,
foreign key (address_code)
references road_address (address_code));

create table jibun_address(
address_code varchar2(50),
province varchar2(80) not null,
district varchar2(80) null,
town varchar2(80) not null,
lot_main number(4,0) not null,
lot_sub number(4,0) not null,
foreign key (address_code)
references road_address (address_code));