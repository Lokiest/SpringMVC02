drop table spring_board;

create table spring_board(
    num number(8) primary key,
    name varchar2(30) not null,
    passwd varchar2(20) not null,
    subject varchar2(200) not null,
    content varchar2(2000),
    wdate date default sysdate,
    readnum number(8) default 0,
    filename varchar2(500),
    originFilename varchar2(500),
    filesize number(8),
    refer number(8), -- �� �׷��ȣ
    lev number(8), --�亯 ����
    sunbun number(8) --���� �� �׷� ���� ���� ���Ľ� �ʿ��� �÷�
);

drop sequence spring_board_seq;
create sequence spring_board_seq
start with 1
increment by 1
nocache;