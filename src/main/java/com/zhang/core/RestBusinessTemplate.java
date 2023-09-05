package com.zhang.core;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author yanlv
 * @version 0.1 : RestBusinessTemplate v0.1 2016/12/1 下午4:20 yanlv Exp $
 */

@Slf4j
public class RestBusinessTemplate {

	private static final Logger BIZ_LOG = LoggerFactory.getLogger("bizCoreException");

	/**
	 * 通过模板执行业务，这里不需要事务
	 * 
	 * @param callback
	 * @return
	 */
	public static <T> CommonRestResult<T> execute(Callback<T> callback) {
		return doExecute(callback);
	}



	private static <T> CommonRestResult<T> doExecute(Callback<T> callback) {

		CommonRestResult<T> restResult = new CommonRestResult<T>();
		restResult.setMessage("");
		try {
			restResult.setStatus(CommonRestResult.SUCCESS);

			T t = null;
			callback.before();
			t = callback.doExecute();
			callback.after(t);

			restResult.setContent(t);
		} catch (BizCoreException e) {
			// 这里是业务异常
			BIZ_LOG.error("业务异常:code={}, message={}", e.getCode(), e.getMessage());

			restResult.setCode(ErrorCode.SYSTEM_EXCEPTION.getCode());
			restResult.setStatus(CommonRestResult.FAIL);
			restResult.setMessage(e.getMessage());

			// 这里设置特殊业务吗
			if (e.getCode() != null) {
				restResult.setCode(e.getCode().getCode());
			}
			throw new BizCoreException("走到了这里");
		} catch (Exception e) {
			// 这里是系统异常
			log.error("系统异常", e);
			callback.callback(e);
			restResult.setCode(ErrorCode.SYSTEM_EXCEPTION.getCode());
			restResult.setStatus(CommonRestResult.FAIL);
			restResult.setMessage(e.getMessage());
		}

		return restResult;
	}

	/**
	 * 执行回调
	 * 
	 * @param <T>
	 */
	public interface Callback<T> {

		/**
		 * 前置检查
		 */
		default void before() {
		}

		/**
		 * 处理业务逻辑
		 * @return
		 */
		public T doExecute();

		/**
		 * 后置检查.
		 */
		default void after(T t) {
		}

		/**
		 * 运行时异常处理
		 */
		default void callback(Exception e){

		}
	}
}
