package com.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingAspect {
	
	@Pointcut("execution(public * com.board..*(..))")
	private void loggingTarget() {
		/*@Pointcut을 특정 메서드 앞에 기술할 수 있다.
		 * 이때 주의사항 @Pointcut이 적용된 메서드는 반환타입이 void여야 한다.
		 * */
	}
	
	@Around("loggingTarget()")
	public Object trace(ProceedingJoinPoint jp) throws Throwable {
		Object[] args = jp.getArgs();
		String str = jp.getSignature().toShortString();
		System.out.println(str + " start .. ");
		if(args!=null) {
			for(Object arg: args) {
				System.out.println("parameters : " + arg.toString());
			}
		}
		long start = System.nanoTime();
		try {
			Object result = jp.proceed();
			return result;
		} finally {
			long end = System.nanoTime();
			System.out.println(str + " end .. ");
			System.out.println(str + " time = " + (end - start) + "ns");
			System.out.println("=======================================");
		}
	}
	
}
