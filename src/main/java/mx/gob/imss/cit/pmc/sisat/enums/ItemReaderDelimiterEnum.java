package mx.gob.imss.cit.pmc.sisat.enums;

import org.springframework.batch.item.file.transform.Range;

import lombok.Getter;
import lombok.Setter;

public enum ItemReaderDelimiterEnum {

	RANGOS_SISAT(new Range[] { new Range(1, 3), new Range(4, 20), new Range(21, 22), new Range(23, 24),
			new Range(25, 27), new Range(28, 29), new Range(30, 31), new Range(32, 34), new Range(35, 37),
			new Range(38, 48), new Range(49, 59), new Range(60, 77), new Range(78, 127), new Range(128, 128),
			new Range(129, 129), new Range(130, 137), new Range(138, 145), new Range(146, 153), new Range(154, 161),
			new Range(162, 169), new Range(170, 177), new Range(178, 185), new Range(186, 188), new Range(189, 191),
			new Range(192, 198), new Range(199, 199), new Range(200, 203), new Range(204, 212), new Range(213, 221),
			new Range(222, 225), new Range(226, 229), new Range(230, 233), new Range(234, 237), new Range(238, 242),
			new Range(243, 243) }),

	RANGOS_SISAT_CARGA_INICIAL(new Range[] { new Range(1, 3), new Range(4, 12), new Range(13, 14), new Range(15, 16),
			new Range(17, 19), new Range(20, 21), new Range(22, 23), new Range(24, 26), new Range(27, 29),
			new Range(30, 40), new Range(41, 51), new Range(52, 69), new Range(70, 119), new Range(120, 120),
			new Range(121, 121), new Range(122, 129), new Range(130, 137), new Range(138, 145), new Range(146, 153),
			new Range(154, 161), new Range(162, 169), new Range(170, 177), new Range(178, 180), new Range(181, 183),
			new Range(184, 190), new Range(191, 191), new Range(192, 195), new Range(196, 204), new Range(205, 213),
			new Range(214, 217), new Range(218, 221), new Range(222, 225), new Range(226, 229), new Range(230, 234),
			new Range(235, 235), new Range(236, 305), new Range(306, 318), new Range(319, 320), new Range(321, 322), 
			new Range(323, 326), new Range(327, 330), new Range(331, 480), new Range(481, 487) });

	@Setter
	@Getter
	private Range[] valores;

	private ItemReaderDelimiterEnum(Range[] valores) {
		this.valores = valores;
	}

}
