package com.github.zuihou.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.zuihou.base.request.PageParams;
import com.github.zuihou.base.request.RequestParams;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.exception.BizException;
import com.github.zuihou.exception.code.BaseExceptionCode;
import com.github.zuihou.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SuperController
 *
 * @author Caratacus
 */
public abstract class BaseController2<S extends IService<Entity>, Id extends Serializable, Entity, PageDTO, SaveDTO, UpdateDTO> {

    Class<Entity> entityClass = null;
    @Autowired
    private S baseService;

    protected BaseController2() {
        entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }

    protected static IPage getPage(PageParams params) {
        return params.getPage();
    }

    protected Class<Entity> getEntityClass() {
        return entityClass;
    }

    /**
     * 成功返回
     *
     * @param data
     * @return
     */
    protected <T> R<T> success(T data) {
        return R.success(data);
    }

    protected R<Boolean> success() {
        return R.success();
    }

    /**
     * 失败返回
     *
     * @param msg
     * @return
     */
    protected <T> R<T> fail(String msg) {
        return R.fail(msg);
    }

    protected <T> R<T> fail(String msg, Object... args) {
        return R.fail(msg, args);
    }

    /**
     * 失败返回
     *
     * @param code
     * @param msg
     * @return
     */
    protected <T> R<T> fail(int code, String msg) {
        return R.fail(code, msg);
    }

    protected <T> R<T> fail(BaseExceptionCode exceptionCode) {
        return R.fail(exceptionCode);
    }

    protected <T> R<T> fail(BizException exception) {
        return R.fail(exception);
    }

    protected <T> R<T> fail(Throwable throwable) {
        return R.fail(throwable);
    }

    protected <T> R<T> validFail(String msg) {
        return R.validFail(msg);
    }

    protected <T> R<T> validFail(String msg, Object... args) {
        return R.validFail(msg, args);
    }

    protected <T> R<T> validFail(BaseExceptionCode exceptionCode) {
        return R.validFail(exceptionCode);
    }

    /**
     * 获取当前用户id
     */
    protected Long getUserId() {
        return BaseContextHandler.getUserId();
    }

    protected String getTenant() {
        return BaseContextHandler.getTenant();
    }

    protected String getAccount() {
        return BaseContextHandler.getAccount();
    }

    protected String getName() {
        return BaseContextHandler.getName();
    }

    /**
     * 根据 bean字段 反射出 数据库字段
     *
     * @param beanField
     * @param clazz
     * @return
     */
    protected String getDbField(String beanField, Class<?> clazz) {
        Field field = ReflectUtil.getField(clazz, beanField);
        TableField tf = field.getAnnotation(TableField.class);
        if (tf != null && StringUtils.isNotEmpty(tf.value())) {
            String str = tf.value();
            return str;
        }
        return beanField;
    }

    /**
     * 计算开始时间
     *
     * @param time
     * @return
     */
    protected String getStartTime(String time) {
        if (time.matches("^\\d{4}-\\d{1,2}$")) {
            return time + "-01 00:00:00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return time + " 00:00:00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return time + ":00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T{1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
            String str = time.replace("T", " ").substring(0, time.indexOf("."));
            return str;
        } else {
            return time;
        }
    }

    /**
     * 计算结束时间
     *
     * @param time
     * @return
     */
    protected String getEndTime(String time) {
        if (time.matches("^\\d{4}-\\d{1,2}$")) {
            Date date = DateUtils.parse(time, "yyyy-MM");
            date = DateUtils.getLastDateOfMonth(date);
            return DateUtils.formatAsDate(date) + " 23:59:59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return time + " 23:59:59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return time + ":59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T{1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
            time = time.replace("T", " ").substring(0, time.indexOf("."));
            if (time.endsWith("00:00:00")) {
                time = time.replace("00:00:00", "23:59:59");
            }
            return time;
        } else {
            return time;
        }
    }


    protected void handlerWrapper(QueryWrapper wrapper, Map<String, String> map, RequestParams<PageDTO> params) {
        if (CollUtil.isNotEmpty(params.getMap())) {
            //拼装区间
            for (Map.Entry<String, String> field : map.entrySet()) {
                String key = field.getKey();
                String value = field.getValue();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                if (key.endsWith("_st")) {
                    String beanField = StrUtil.subBefore(key, "_st", true);
                    wrapper.ge(getDbField(beanField, entityClass), getStartTime(value));
                }
                if (key.endsWith("_ed")) {
                    String beanField = StrUtil.subBefore(key, "_ed", true);
                    wrapper.le(getDbField(beanField, entityClass), getEndTime(value));
                }
            }
        }
//        Class<?> clazz = null;
//        if (params.getModel() == null) {
//            try {
//                clazz = params.getClass().getSuperclass().getDeclaredField("model").getDeclaringClass();
//            } catch (NoSuchFieldException e) {
//                throw new BizException(BASE_VALID_PARAM, "参数不对");
//            }
//        } else {
//            clazz = params.getModel().getClass();
//        }

        handlerQuery(wrapper, params);
    }

    /**
     * 客户自定义处理查询参数组装
     *
     * @param wrapper
     * @param params
     */
    protected void handlerQuery(QueryWrapper wrapper, RequestParams<PageDTO> params) {

    }

    @ApiOperation(value = "查询分页列表")
    @PostMapping(value = "/page")
    public R<IPage<Entity>> page(@RequestBody @Validated RequestParams<PageDTO> params) {
        IPage<Entity> page = params.getPage();
        Entity model = BeanUtil.toBean(params.getModel(), entityClass);
        QueryWrapper<Entity> wrapper = Wrappers.query(model);

        handlerWrapper(wrapper, params.getMap(), params);
        baseService.page(page, wrapper);
        handlerResult(page);
        return success(page);
    }

    /**
     * 自定义处理返回结果
     *
     * @param records
     */
    protected void handlerResult(IPage<Entity> records) {
        // 调用注入方法
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public R<Entity> save(@RequestBody @Valid SaveDTO saveDTO) {
        R<Entity> result = handlerSave(saveDTO);
        if (result.getDefExec()) {
            Entity model = BeanUtil.toBean(saveDTO, entityClass);
            baseService.save(model);
            result.setData(model);
        }
        return result;
    }

    /**
     * 用户自定义新增
     *
     * @param saveDTO
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    protected R<Entity> handlerSave(SaveDTO saveDTO) {
        return R.success(true);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping
    public R<Boolean> delete(@RequestParam("ids[]") List<Id> ids) {
        R<Boolean> result = handlerDelete(ids);
        if (result.getDefExec()) {
            baseService.removeByIds(ids);
        }
        return result;
    }

    /**
     * 自定义删除
     *
     * @param ids
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    protected R<Boolean> handlerDelete(List<Id> ids) {
        return R.success(true, true);
    }

    @ApiOperation(value = "修改")
    @PutMapping
    public R<Entity> update(@RequestBody UpdateDTO updateDTO) {
        R<Entity> result = handlerUpdate(updateDTO);
        if (result.getDefExec()) {
            Entity model = BeanUtil.toBean(updateDTO, entityClass);
            baseService.updateById(model);
            result.setData(model);
        }
        return result;
    }

    /**
     * 自定义更新
     *
     * @param model
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    protected R<Entity> handlerUpdate(UpdateDTO model) {
        return R.success(true);
    }

    /**
     * 查询用户
     *
     * @param id 主键id
     * @return 查询结果
     */
    @ApiOperation(value = "查询用户", notes = "查询用户")
    @GetMapping("/{id}")
    public R<Entity> get(@PathVariable Id id) {
        return success(baseService.getById(id));
    }

}
