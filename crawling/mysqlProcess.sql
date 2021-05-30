#### MySQL 반복문 작업
-- DELIMITER $$ 
-- CREATE PROCEDURE myFunction2() -- ⓐ myFunction이라는 이름의 프로시져
-- BEGIN
--     DECLARE i INT DEFAULT 2; -- ⓑ i변수 선언, defalt값으로 1설정
--     WHILE (i <= 6002) DO -- ⓒ for문 작성(i가 1000이 될 때까지 반복)
--         INSERT INTO `item`(i_type,i_completed) VALUE ("sell",false); -- ⓓ 테이블에 i값 넣어주기
--         SET i = i + 1; -- ⓔ i값에 1더해주고 WHILE문 처음으로 이동
--     END WHILE;
-- END$$
-- DELIMITER ;
-- CALL myFunction2(); -- 프로시저 실행, 테이블에 1~1000까지 숫자 채워주기
-- SELECT * FROM item;
###############################################################################################

### 조인을 통한... 아무튼 되는 출력문
-- select * from (select * from item_sell
--    inner join 
--    (select ANY_VALUE(ip_no) as ip_no, ip_item_no, ANY_VALUE(ip_value) as ip_value from item_photo group by ip_item_no)
--    tmp_table on is_item_no = tmp_table.ip_item_no) final_table1
-- left join (select ap_item_no, count(*) as joiner_cnt from auction_participant group by ap_item_no) final_table2 on final_table1.ip_item_no = final_table2.ap_item_no
-- order by is_no
-- limit 1, 100;
###############################################################################################