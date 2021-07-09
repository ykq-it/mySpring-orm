package com.my.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 功能描述：
 * @Column：标识对应数据库的哪个字段，如果没有注解，则默认为数据库字段与成员变量相同；还可标识该字段是否可插入、可更新
 * @Transient：在已有的数据库实体类中再增加一个成员变量，且该变量不参与数据库的读写
 * @Id：在dao层getPKColumn如果没有返回主键的字段名，那么在实体类中找被@Id注解的成员变量作为书剑
 * @author ykq
 * @date 2021/06/10 10:33 AM
 * @param
 * @return
 */

@Entity
@Table(name="t_member")
@Data
public class Member implements Serializable {
    @Id private Long id;
    private String name;
    private String addr;
    private Integer age;

    public Member() {
    }

    public Member(String name, String addr, Integer age) {
        this.name = name;
        this.addr = addr;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", age=" + age +
                '}';
    }
}
