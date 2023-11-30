package mx.gob.imss.cit.pmc.sisat.repository.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import mx.gob.imss.cit.pmc.commons.dto.BitacoraConsultaST3;
import mx.gob.imss.cit.pmc.commons.dto.CountDTO;
import mx.gob.imss.cit.pmc.sisat.repository.BitacoraConsultaST3Repository;

@Repository
public class BitacoraConsultaST3RepositoryImpl implements BitacoraConsultaST3Repository{

	private static final Logger logger = LoggerFactory.getLogger(BitacoraConsultaST3RepositoryImpl.class);
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Override
	public Long getCountBitacora(Date fInicio, Date fFin) {
		long totalElements =0;
		
		List<AggregationOperation> aggregationOperationList = buildAggregationOperationList(fInicio, fFin);
		
		CountOperation count = Aggregation.count().as("totalElements");
		aggregationOperationList.add(count);
		
		TypedAggregation<CountDTO> aggregation = Aggregation.newAggregation(CountDTO.class, aggregationOperationList)
				.withOptions(AggregationOptions.builder().allowDiskUse(Boolean.TRUE).build());
		logger.info(aggregation.toString());
		AggregationResults<CountDTO> countAggregationResult = mongoOperations.aggregate(aggregation, BitacoraConsultaST3.class,
				CountDTO.class);
		totalElements = !countAggregationResult.getMappedResults().isEmpty()
				&& countAggregationResult.getMappedResults().get(0) != null
						? countAggregationResult.getMappedResults().get(0).getTotalElements()
						: 0;
		logger.info("Elemetos totales de la busqueda: " + totalElements);
		logger.info("----------------------------------------------------");
		return totalElements;
		
	}
	
	private List<AggregationOperation> buildAggregationOperationList(Date fInicio, Date fFin) {
        List<AggregationOperation> aggregationOperationList = null;

		Criteria cFecProcesoCarga = null;
		cFecProcesoCarga = new Criteria().andOperator(Criteria.where("fecConsulta").gt(fInicio),
				Criteria.where("fecConsulta").lte(fFin));
		
		aggregationOperationList = Arrays.asList(validateMatchOp(cFecProcesoCarga));
		aggregationOperationList = aggregationOperationList.stream().filter(Objects::nonNull)
				.collect(Collectors.toList());
		return aggregationOperationList;
		
	}
	
	private MatchOperation validateMatchOp(Criteria criterio) {
		return criterio != null ? Aggregation.match(criterio) : null;
	}
	
}
