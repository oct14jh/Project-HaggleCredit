SELECT count(*) FROM haggle_credit.item_sell;

DELIMITER $$ 
CREATE PROCEDURE myFunction3() -- ⓐ myFunction이라는 이름의 프로시져
BEGIN
    DECLARE i INT DEFAULT 1; -- ⓑ i변수 선언, defalt값으로 1설정
    WHILE (i <= 6002) DO -- ⓒ for문 작성(i가 1000이 될 때까지 반복)
        update haggle_credit.item_sell set is_user_no=floor(1+RAND()*4) where is_no = i; -- ⓓ 테이블에 i값 넣어주기
        SET i = i + 1; -- ⓔ i값에 1더해주고 WHILE문 처음으로 이동
    END WHILE;
END$$
DELIMITER ;
CALL myFunction3(); -- 프로시저 실행, 테이블에 1~1000까지 숫자 채워주기
SELECT * FROM haggle_credit.item_sell;


