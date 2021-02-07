## 요건
SQL과 필터조건을 실행해서 추출 또는 조회하거나, 테이블의 샘플데이터를 추출 또는 조회할 수 있다.
<br>
Oracle, Hive, PostgreSQL 등 다양한 DB에 적용할 수 있다.

## Project 구성
1. JPA nativeQuery 적용
2. Unit Test - Junit 5
3. [API Documents - Rest Docs](localhost:8080/docs/index.html)    
   * AsciiDocs skin - adoc-colony.css

## AsciiDocs Skin 적용
1. Luby 설치
2. gem install asciidoctor
3. adoc-colony.css를 asciidoc 폴더로 copy 
4. index.adoc파일에 내용 추가
```
:stylesheet: adoc-colony.css
```
5. index.adoc에 skin 적용
```shell
asciidoctor index.adoc
```
6. Maven package
```shell
mvn package
```

## JPA Multi DataSource, em.createNativeQuery
1. CrudRepository/JpaRepository Interface를 사용하는 경우
   - basePackages 를 통해 Repository 위치지정
   - builder.packages("me.kalpha.jdbctemplate.batch")를 통해 Entity 위치지정
2. EntityManager를 직접 사용하는 경우
   - DataSourceConfig에서 EntityManager Bean 등록시 builder.persistenceUnit(Constants.BATCH_UNIT_NAME)를 통해 Bean 이름 등록
   - Repository에서 EntityManager Injection 시에 @PersistenceContext(unitName = Constants.BATCH_UNIT_NAME)을 통해 해당 Bean 호출
