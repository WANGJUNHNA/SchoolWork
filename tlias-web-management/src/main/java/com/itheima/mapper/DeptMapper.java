package com.itheima.mapper;
import com.itheima.pojo.Dept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper//告诉SpringBoot这是一个Mapper类，核心作用是定义对数据库中dept表的查询操作，并利用 MyBatis 的注解特性，将 Java 方法与 SQL 语句直接绑定
public interface DeptMapper {
    /*
    * 查询所有的部门数据
    * */
    @Select("select id,name,create_time,update_time from dept order by update_time desc ")
    List<Dept> findAll();//查询所有部门数据

    /*
    * 根据ID删除部门
    * */
    @Delete("delete  from  dept where id = #{id}")//#{id}：表示占位符，表示id参数的值,占位符的值会从方法参数中获取,占位符的值会替换掉#{id},表示预编译的sql
    void deleteById(Integer id);

    /*
    * 新增部门
    * */ //字段名是下划线；属性名是驼峰命名
    @Insert("insert into dept(name,create_time,update_time) values(#{name},#{createTime},#{updateTime})")//这里注意在#后面不能写creat_time，因为creat_time是数据库中的字段，不能作为参数
    void insert(Dept dept);

    /*
    * 根据ID查询部门
    * */
    @Select("select id,name,create_time,update_time from dept where id = #{id}")
    Dept getById(Integer id);

    /*
    * 修改部门
    * */
    @Insert("update dept set name = #{name},update_time = #{updateTime} where id = #{id}")
    void update(Dept dept);
}
