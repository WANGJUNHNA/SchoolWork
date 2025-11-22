package com.itheima.controller;
import com.itheima.pojo.Dept;
import com.itheima.pojo.Result;
import com.itheima.service.DeptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//@Slf4j// 日志记录
@RequestMapping("/depts") //优化代码，将公共的请求路径前缀统一抽取到类上
@RestController
public class DeptController {
    //用@Slf4j注解，将日志记录功能封装到类中，不用再写日志记录代码，简化下面一长串代码
    private static final org.slf4j.Logger log  = LoggerFactory.getLogger(DeptController.class);

    @Autowired
    private DeptService deptService;
    //查询部门
    //@RequestMapping(value = "/depts",method = RequestMethod.GET) //method: 指定请求的方式
    //@GetMapping(value = "/depts", produces = MediaType.APPLICATION_JSON_VALUE)//spring框架中指定的请求方式，指定返回数据为json格式
    @GetMapping
    public Result list(){
        //System.out.println("查询全部部门数据");//测试代码,输出到控制台
        log.info("查询全部部门数据");
        List<Dept> deptList =  deptService.findAll();
        return Result.success(deptList);
    }

    //删除部门
    /*
    * 方式一：基于HttpServletRequest对象获取参数(操作还需转化，企业用的不多)
    * */
//    @DeleteMapping("/depts")
//    public Result delete(HttpServletRequest request){
//        String idStr = request.getParameter("id");//通过getParameter方法获取的参数都是String类型
//        int i = Integer.parseInt(idStr);//但是对于ID指来说需要转换成int类型，拿到int类型的id值
//        System.out.println("删除部门：" + i);
//        return Result.success();
//    }
    /*
    * 方式二：基于Spring提供的@RequestParam注解获取参数，会自动进行类型转化，将请求参数绑定给方法参数
    * 注意事项：一旦声明了@RequestParam注解，那么这个参数的id值必须存在，否则会报错（400错误）
    * */
    @DeleteMapping
    public Result delete(@RequestParam("id") Integer deptId){
        //System.out.println("删除部门：" + deptId);
        log.info("删除部门：{}" ,deptId);
        deptService.deleteById(deptId);
        return Result.success();
    }

    /*
    * 新增部门
    * */
    @PostMapping
    public Result add(@RequestBody Dept dept){//@RequestBody注解可以将一个json格式的请求参数，直接封装到一个对象当中
        //System.out.println("新增部门：" + dept);
        deptService.add(dept);
        return Result.success();
    }

    /*
    * 根据ID查询部门
    * */
//    @GetMapping("/depts/{id}")
//    public Result getInfo(@PathVariable("id") Integer deptId){
//        System.out.println("根据ID查询部门：" + deptId);
//        return Result.success();
//    }

    /*
     * 根据ID查询部门(如果这个路径参数名和这个方法的形参名称一致，即可省略一部分简化写)
     * */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        //System.out.println("根据ID查询部门：" + id);
        log.info("根据ID查询部门：{}" ,id);
        Dept dept = deptService.getById(id);//调用service层方法查询部门数据有返回数据所以需要设置返回值
        return Result.success(dept);//返回值给前端
    }

    /*
     * 修改部门
     * */
    @PutMapping
    public Result update(@RequestBody Dept dept){
        //System.out.println("修改部门：" + dept);
        log.info("修改部门：{}" ,dept);
        deptService.update(dept);
        return Result.success();
    }
}

