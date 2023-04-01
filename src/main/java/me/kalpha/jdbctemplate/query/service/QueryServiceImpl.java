package me.kalpha.jdbctemplate.query.service;

import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.config.EntityManagerConfig;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * {@link EntityManagerConfig}의 Named EntityManager를 Injection해서 Repository 생성자로 전달한다.
 */
@Service
public class QueryServiceImpl implements QueryService {
    private QueryRepository queryRepository;
    private QueryRepositoryOthersImpl batchQueryRepository;
    private QueryRepositoryOthersImpl ehubQueryRepository;
    private ModelMapper mapper;

    @Autowired
    public QueryServiceImpl(@Qualifier(Constants.SYS_BATCH) EntityManager batchEntityManager,
                            @Qualifier(Constants.SYS_EHUB) EntityManager ehubEntityManager,
                            ModelMapper mapper) {
        batchQueryRepository = new QueryRepositoryOthersImpl(batchEntityManager);
        ehubQueryRepository = new QueryRepositoryOthersImpl(ehubEntityManager);
        this.mapper = mapper;
    }


    @Override
    public SampleResponse findSample(SamplesDto samplesDto) {
        setRepository(samplesDto.getSystemId());
        List<Map<String,Object>> result = queryRepository.findSamples(samplesDto);

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

        List list = findTable(tableVo);
        String fileName = String.format("%s-%s", tableVo.getTable().getFrom(), tableVo.getRequiredTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        return saveResult(list, fileName);
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

    private List<Object[]> findTable(TableVo tableVo) {
        return queryRepository.findTable(tableVo);
    }

    private List<Object[]> findByQuery(QueryDto queryDto) {
        return queryRepository.findByQuery(queryDto);
    }

    private void setRepository(String systemId) {
        switch (systemId) {
            case Constants.SYS_BATCH:
                queryRepository = batchQueryRepository;
                break;
            case Constants.SYS_EHUB:
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
