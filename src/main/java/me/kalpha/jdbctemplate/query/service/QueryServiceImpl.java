package me.kalpha.jdbctemplate.query.service;

import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.common.QueryProperties;
import me.kalpha.jdbctemplate.query.dto.*;
import me.kalpha.jdbctemplate.query.repository.QueryRepository;
import me.kalpha.jdbctemplate.query.repository.QueryRepositoryOthersImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * {@link EntityManagerConfig}의 Named EntityManager를 Injection해서 Repository 생성자로 전달한다.
 */

@Transactional
@Service
public class QueryServiceImpl implements QueryService {
    private QueryRepository queryRepository;
    private QueryRepositoryOthersImpl batchQueryRepository;
    private QueryRepositoryOthersImpl ehubQueryRepository;
    private ModelMapper mapper;
    private QueryProperties properties;

    @Autowired
    public QueryServiceImpl(@Qualifier(Constants.BATCH_UNIT_NAME) EntityManager batchEntityManager,
                            @Qualifier(Constants.EHUB_UNIT_NAME) EntityManager ehubEntityManager,
                            ModelMapper mapper,
                            QueryProperties properties) {
        batchQueryRepository = new QueryRepositoryOthersImpl(batchEntityManager);
        ehubQueryRepository = new QueryRepositoryOthersImpl(ehubEntityManager);
        this.mapper = mapper;
        this.properties = properties;
    }

    @Override
    public SampleResponse findSample(SamplesDto samplesDto) {
        setRepository(samplesDto.getSystemId());
        List<Map<String,Object>> result = queryRepository.findSamples(samplesDto, properties.getSamplesCount());

        SampleResponse response = mapper.map(samplesDto, SampleResponse.class);
        response.setRecords(result);

        return response;
    }

    @Override
    public List<QueryResult> findTable(TableDto tableDto, Long limit) {
        TableVo tableVo = mapper.map(tableDto, TableVo.class);
        tableVo.updateSqlFromTable();

        setRepository(tableDto.getSystemId());
        return queryRepository.findTable(tableVo, tableVo.getLimit());
    }

    @Override
    public long extractTable(TableDto tableDto) {
        TableVo tableVo = mapper.map(tableDto, TableVo.class);
        tableVo.updateSqlFromTable();
        setRepository(tableVo.getSystemId());

        List columnNames = null;
        List records = null;
        try {
            Object[] o = findTable(tableVo);
            columnNames = (List<String>) o[0];
            records = (List<Object[]>) o[1];

            columnNames.stream().forEach(System.out::print);
            System.out.println();
            records.stream().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileName = String.format("%s-%s", tableVo.getTable().getFrom(), tableVo.getRequiredTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        return saveResult(records, fileName);
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto) {
        TableVo tableVo = mapper.map(tableDto, TableVo.class);
        tableVo.updateSqlFromTable();
        setRepository(tableVo.getSystemId());

        return queryRepository.findTable(pageable, tableVo);
    }

    @Override
    public Boolean validateSql(QueryDto queryDto) {
        setRepository(queryDto.getSystemId());
        return queryRepository.validateSql(queryDto);
    }

    @Override
    public List<QueryResult> findByQuery(QueryDto queryDto, Long limit) {
        setRepository(queryDto.getSystemId());
        return queryRepository.findByQuery(queryDto, queryDto.getLimit());
    }

    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        setRepository(queryDto.getSystemId());
        return queryRepository.findByQuery(pageable, queryDto);
    }

    @Override
    public long extractByQuery(QueryDto queryDto) {
        setRepository(queryDto.getSystemId());
        String fileName= String.format("%s-%s", queryDto.getSystemId(), queryDto.getRequiredTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        return saveResult(queryRepository.findByQuery(queryDto), fileName);
    }

    //-------------------------------------------------

    private Object[] findTable(TableVo tableVo) throws SQLException {
        return queryRepository.findTable(tableVo);
    }

    private List<Object[]> findByQuery(QueryDto queryDto) {
        return queryRepository.findByQuery(queryDto);
    }

    private void setRepository(String systemId) {
        switch (systemId) {
            case Constants.BATCH_UNIT_NAME:
                queryRepository = batchQueryRepository;
                break;
            case Constants.EHUB_UNIT_NAME:
                queryRepository = ehubQueryRepository;
        }
    }

    private long saveResult(List list, String filename) {
        for (int i=0; i<list.size(); i++) {
            Object[] ov = (Object[]) list.get(i);
            StringBuffer sb = new StringBuffer();
            for (int j=0; j< ov.length; j++) {
                if (j == 0) {
                    sb.append(ov[j]);
                } else {
                    sb.append("\t"+ov[j]);
                }
            }
            //TO-DO the code for writing to Isilon
            System.out.println(sb);
            //<--
        }
        return list.size();
    }

}
