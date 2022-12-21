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
    refer number(8), -- 글 그룹번호
    lev number(8), --답변 레벨
    sunbun number(8) --같은 글 그룹 내에 순서 정렬시 필요한 컬럼
);

drop sequence spring_board_seq;
create sequence spring_board_seq
start with 1
increment by 1
nocache;