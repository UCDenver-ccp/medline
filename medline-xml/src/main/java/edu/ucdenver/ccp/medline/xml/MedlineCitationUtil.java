package edu.ucdenver.ccp.medline.xml;

import org.medline.AbstractText;
import org.medline.MedlineCitation;

import edu.ucdenver.ccp.common.string.StringUtil;

public class MedlineCitationUtil {

	/**
	 * Consolidates all sections of an abstract into a single {@link String}
	 * 
	 * @param citation
	 * @return a String representation of the Abstract
	 */
	public static String getAbstractText(MedlineCitation citation) {
		StringBuffer buffer = new StringBuffer();
		if (citation.getArticle().getAbstract() == null) {
			return null;
		}
		for (AbstractText abstractText : citation.getArticle().getAbstract().getAbstractText()) {
			String label = abstractText.getLabel();
			String text = abstractText.getvalue();
			if (label != null) {
				buffer.append(label + ": " + text + "\n");
			} else {
				buffer.append(text + "\n");
			}
		}
		return StringUtil.removeLastCharacter(buffer.toString());
	}
}
