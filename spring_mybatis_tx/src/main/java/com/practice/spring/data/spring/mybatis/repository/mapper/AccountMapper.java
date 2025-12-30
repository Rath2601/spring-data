package com.practice.spring.data.spring.mybatis.repository.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface AccountMapper {

    @Update("UPDATE account SET balance = balance - #{amount} WHERE id = #{id}")
    void debit(@Param("id") long id, @Param("amount") int amount);

    @Update("UPDATE account SET balance = balance + #{amount} WHERE id = #{id}")
    void credit(@Param("id") long id, @Param("amount") int amount);

    @Select("SELECT balance FROM account WHERE id = #{id}")
    Integer getBalance(long id);

    @Insert("""
        INSERT INTO transaction_log(account_id, amount, status)
        VALUES (#{accountId}, #{amount}, #{status})
    """)
    void log(@Param("accountId") long accountId,
             @Param("amount") int amount,
             @Param("status") String status);
}

