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