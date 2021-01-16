package me.kalpha.jdbctemplate.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP를 사용하면 모든 Class에 공통로직(cross cutting concern)을 넣을 수 있다.
 * TimeTraceAOP는 me.kalpha.jdbtemplate.query Package아래의 모든 Class의 Method들의 실행시간을 측정한다.
 */
@Aspect
@Component
public class TimeTraceAop {

    @Around("execution(* me.kalpha.jdbctemplete.query..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("START : " + joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long duration = finish - start;
            System.out.println("END : " + joinPoint.toString() + " " + duration + "ms");
        }
    }
}
