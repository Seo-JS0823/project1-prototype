create table Industry_Major(
major_code varchar2(5) primary key,
major_name varchar2(20) not null
);

create table Industry_sub(
major_code varchar2(5),
sub_code varchar2(3) not null,
sub_name varchar2(50) not null,
primary key (major_code, sub_code),
FOREIGN KEY (major_code) references Industry_Major (major_code)
);