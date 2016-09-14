package org.cbioportal.model;

import java.io.Serializable;

public class Mutation implements Serializable {

    private Integer mutationEventId;
    private MutationEvent mutationEvent;
    private String geneticProfileId;
    private GeneticProfile geneticProfile;
    private String sampleId;
    private Sample sample;
    private Integer entrezGeneId;
    private Gene gene;
    private String sequencingCenter;
    private String sequencer;
    private String mutationStatus;
    private String validationStatus;
    private String tumorSeqAllele1;
    private String tumorSeqAllele2;
    private String matchedNormSampleBarcode;
    private String matchNormSeqAllele1;
    private String matchNormSeqAllele2;
    private String tumorValidationAllele1;
    private String tumorValidationAllele2;
    private String matchNormValidationAllele1;
    private String matchNormValidationAllele2;
    private String verificationStatus;
    private String sequencingPhase;
    private String sequenceSource;
    private String validationMethod;
    private String score;
    private String bamFile;
    private Integer variantReadCountTumor;
    private Integer referenceReadCountTumor;
    private Integer variantReadCountNormal;
    private Integer referenceReadCountNormal;
    private String aminoAcidChange;
	private Float dipLogR;
	private Float segMean;
	private Float cellularFraction;
	private Integer totalCopyNumber;
	private Integer minorCopyNumber;
	private Float purity;
	private Float ploidy;
	private Float ccfMCopies;
	private Float ccfMCopiesLower;
	private Float ccfMCopiesUpper;
	private Float ccfMCopiesProb95;
	private Float ccfMCopiesProb90;
	private Float ccf1Copy;
	private Float ccf1CopyLower;
	private Float ccf1CopyUpper;
	private Float ccf1CopyProb95;
	private Float ccf1CopyProb90;

    public Integer getMutationEventId() {
        return mutationEventId;
    }

    public void setMutationEventId(Integer mutationEventId) {
        this.mutationEventId = mutationEventId;
    }

    public MutationEvent getMutationEvent() {
        return mutationEvent;
    }

    public void setMutationEvent(MutationEvent mutationEvent) {
        this.mutationEvent = mutationEvent;
    }

    public String getGeneticProfileId() {
        return geneticProfileId;
    }

    public void setGeneticProfileId(String geneticProfileId) {
        this.geneticProfileId = geneticProfileId;
    }

    public GeneticProfile getGeneticProfile() {
        return geneticProfile;
    }

    public void setGeneticProfile(GeneticProfile geneticProfile) {
        this.geneticProfile = geneticProfile;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Integer getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(Integer entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public String getSequencingCenter() {
        return sequencingCenter;
    }

    public void setSequencingCenter(String sequencingCenter) {
        this.sequencingCenter = sequencingCenter;
    }

    public String getSequencer() {
        return sequencer;
    }

    public void setSequencer(String sequencer) {
        this.sequencer = sequencer;
    }

    public String getMutationStatus() {
        return mutationStatus;
    }

    public void setMutationStatus(String mutationStatus) {
        this.mutationStatus = mutationStatus;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getTumorSeqAllele1() {
        return tumorSeqAllele1;
    }

    public void setTumorSeqAllele1(String tumorSeqAllele1) {
        this.tumorSeqAllele1 = tumorSeqAllele1;
    }

    public String getTumorSeqAllele2() {
        return tumorSeqAllele2;
    }

    public void setTumorSeqAllele2(String tumorSeqAllele2) {
        this.tumorSeqAllele2 = tumorSeqAllele2;
    }

    public String getMatchedNormSampleBarcode() {
        return matchedNormSampleBarcode;
    }

    public void setMatchedNormSampleBarcode(String matchedNormSampleBarcode) {
        this.matchedNormSampleBarcode = matchedNormSampleBarcode;
    }

    public String getMatchNormSeqAllele1() {
        return matchNormSeqAllele1;
    }

    public void setMatchNormSeqAllele1(String matchNormSeqAllele1) {
        this.matchNormSeqAllele1 = matchNormSeqAllele1;
    }

    public String getMatchNormSeqAllele2() {
        return matchNormSeqAllele2;
    }

    public void setMatchNormSeqAllele2(String matchNormSeqAllele2) {
        this.matchNormSeqAllele2 = matchNormSeqAllele2;
    }

    public String getTumorValidationAllele1() {
        return tumorValidationAllele1;
    }

    public void setTumorValidationAllele1(String tumorValidationAllele1) {
        this.tumorValidationAllele1 = tumorValidationAllele1;
    }

    public String getTumorValidationAllele2() {
        return tumorValidationAllele2;
    }

    public void setTumorValidationAllele2(String tumorValidationAllele2) {
        this.tumorValidationAllele2 = tumorValidationAllele2;
    }

    public String getMatchNormValidationAllele1() {
        return matchNormValidationAllele1;
    }

    public void setMatchNormValidationAllele1(String matchNormValidationAllele1) {
        this.matchNormValidationAllele1 = matchNormValidationAllele1;
    }

    public String getMatchNormValidationAllele2() {
        return matchNormValidationAllele2;
    }

    public void setMatchNormValidationAllele2(String matchNormValidationAllele2) {
        this.matchNormValidationAllele2 = matchNormValidationAllele2;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getSequencingPhase() {
        return sequencingPhase;
    }

    public void setSequencingPhase(String sequencingPhase) {
        this.sequencingPhase = sequencingPhase;
    }

    public String getSequenceSource() {
        return sequenceSource;
    }

    public void setSequenceSource(String sequenceSource) {
        this.sequenceSource = sequenceSource;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getBamFile() {
        return bamFile;
    }

    public void setBamFile(String bamFile) {
        this.bamFile = bamFile;
    }

    public Integer getVariantReadCountTumor() {
        return variantReadCountTumor;
    }

    public void setVariantReadCountTumor(Integer variantReadCountTumor) {
        this.variantReadCountTumor = variantReadCountTumor;
    }

    public Integer getReferenceReadCountTumor() {
        return referenceReadCountTumor;
    }

    public void setReferenceReadCountTumor(Integer referenceReadCountTumor) {
        this.referenceReadCountTumor = referenceReadCountTumor;
    }

    public Integer getVariantReadCountNormal() {
        return variantReadCountNormal;
    }

    public void setVariantReadCountNormal(Integer variantReadCountNormal) {
        this.variantReadCountNormal = variantReadCountNormal;
    }

    public Integer getReferenceReadCountNormal() {
        return referenceReadCountNormal;
    }

    public void setReferenceReadCountNormal(Integer referenceReadCountNormal) {
        this.referenceReadCountNormal = referenceReadCountNormal;
    }

    public String getAminoAcidChange() {
        return aminoAcidChange;
    }

    public void setAminoAcidChange(String aminoAcidChange) {
        this.aminoAcidChange = aminoAcidChange;
    }

	public Float getDipLogR() {
		return dipLogR;
	}

	public void setDipLogR(Float dipLogR) {
		this.dipLogR = dipLogR;
	}

	public Float getSegMean() {
		return segMean;
	}

	public void setSegMean(Float segMean) {
		this.segMean = segMean;
	}

	public Float getCellularFraction() {
		return cellularFraction;
	}

	public void setCellularFraction(Float cellularFraction) {
		this.cellularFraction = cellularFraction;
	}

	public Integer getTotalCopyNumber() {
		return totalCopyNumber;
	}

	public void setTotalCopyNumber(Integer totalCopyNumber) {
		this.totalCopyNumber = totalCopyNumber;
	}

	public Integer getMinorCopyNumber() {
		return minorCopyNumber;
	}

	public void setMinorCopyNumber(Integer minorCopyNumber) {
		this.minorCopyNumber = minorCopyNumber;
	}

	public Float getPurity() {
		return purity;
	}

	public void setPurity(Float purity) {
		this.purity = purity;
	}

	public Float getPloidy() {
		return ploidy;
	}

	public void setPloidy(Float ploidy) {
		this.ploidy = ploidy;
	}

	public Float getCcfMCopies() {
		return ccfMCopies;
	}

	public void setCcfMCopies(Float ccfMCopies) {
		this.ccfMCopies = ccfMCopies; }

	public Float getCcfMCopiesLower() {
		return ccfMCopiesLower;
	}

	public void setCcfMCopiesLower(Float ccfMCopiesLower) {
		this.ccfMCopiesLower = ccfMCopiesLower;
	}

	public Float getCcfMCopiesUpper() {
		return ccfMCopiesUpper;
	}

	public void setCcfMCopiesUpper(Float ccfMCopiesUpper) {
		this.ccfMCopiesUpper = ccfMCopiesUpper;
	}

	public Float getCcfMCopiesProb95() {
		return ccfMCopiesProb95;
	}

	public void setCcfMCopiesProb95(Float ccfMCopiesProb95) {
		this.ccfMCopiesProb95 = ccfMCopiesProb95;
	}

	public Float getCcfMCopiesProb90() {
		return ccfMCopiesProb90;
	}

	public void setCcfMCopiesProb90(Float ccfMCopiesProb90) {
		this.ccfMCopiesProb90 = ccfMCopiesProb90;
	}

	public Float getCcf1Copy() {
		return ccf1Copy;
	}

	public void setCcf1Copy(Float ccf1Copy) {
		this.ccf1Copy = ccf1Copy;
	}

	public Float getCcf1CopyLower() {
		return ccf1CopyLower;
	}

	public void setCcf1CopyLower(Float ccf1CopyLower) {
		this.ccf1CopyLower = ccf1CopyLower;
	}

	public Float getCcf1CopyUpper() {
		return ccf1CopyUpper;
	}

	public void setCcf1CopyUpper(Float ccf1CopyUpper) {
		this.ccf1CopyUpper = ccf1CopyUpper;
	}

	public Float getCcf1CopyProb95() {
		return ccf1CopyProb95;
	}

	public void setCcf1CopyProb95(Float ccf1CopyProb95) {
		this.ccf1CopyProb95 = ccf1CopyProb95;
	}

	public Float getCcf1CopyProb90() {
		return ccf1CopyProb90;
	}

	public void setCcf1CopyProb90(Float ccf1CopyProb90) {
		this.ccf1CopyProb90 = ccf1CopyProb90;
	}
}
