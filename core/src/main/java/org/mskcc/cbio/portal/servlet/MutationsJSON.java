/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cbio.portal.servlet;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;
import org.cbioportal.persistence.MutationRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.mskcc.cbio.portal.dao.DaoCancerStudy;
import org.mskcc.cbio.portal.dao.DaoCosmicData;
import org.mskcc.cbio.portal.dao.DaoDrugInteraction;
import org.mskcc.cbio.portal.dao.DaoException;
import org.mskcc.cbio.portal.dao.DaoGeneOptimized;
import org.mskcc.cbio.portal.dao.DaoGeneticAlteration;
import org.mskcc.cbio.portal.dao.DaoGeneticProfile;
import org.mskcc.cbio.portal.dao.DaoMutSig;
import org.mskcc.cbio.portal.dao.DaoMutation;
import org.mskcc.cbio.portal.dao.DaoSample;
import org.mskcc.cbio.portal.dao.DaoSangerCensus;
import org.mskcc.cbio.portal.model.CancerStudy;
import org.mskcc.cbio.portal.model.CanonicalGene;
import org.mskcc.cbio.portal.model.CosmicMutationFrequency;
import org.mskcc.cbio.portal.model.ExtendedMutation;
import org.mskcc.cbio.portal.model.GeneticProfile;
import org.mskcc.cbio.portal.model.MutSig;
import org.mskcc.cbio.portal.model.Sample;
import org.mskcc.cbio.portal.model.converter.MutationModelConverter;
import org.mskcc.cbio.portal.util.InternalIdUtil;
import org.mskcc.cbio.portal.util.MyCancerGenomeLinkUtil;
import org.mskcc.cbio.portal.util.OncokbHotspotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author jj
 */
public class MutationsJSON extends HttpServlet {
    private static Logger logger = Logger.getLogger(MutationsJSON.class);
    
    public static final String CMD = "cmd";
    public static final String GET_CONTEXT_CMD = "get_context";
    public static final String GET_DRUG_CMD = "get_drug";
    public static final String COUNT_MUTATIONS_CMD = "count_mutations";
    public static final String GET_SMG_CMD = "get_smg";
    public static final String MUTATION_EVENT_ID = "mutation_id";
    public static final String GENE_CONTEXT = "gene_context";
    public static final String KEYWORD_CONTEXT = "keyword_context";
    public static final String MUTATION_CONTEXT = "mutation_context";

    @Autowired
    private MutationRepository mutationRepository;

    @Autowired
    private MutationModelConverter mutationModelConverter;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String cmd = request.getParameter(CMD);
        if (cmd!=null) {
            if (cmd.equalsIgnoreCase(GET_CONTEXT_CMD)) {
                processGetMutationContextRequest(request, response);
                return;
            }
            
            if (cmd.equalsIgnoreCase(COUNT_MUTATIONS_CMD)) {
                processCountMutationsRequest(request, response);
                return;
            }
            
            if (cmd.equalsIgnoreCase(GET_SMG_CMD)) {
                processGetSmgRequest(request, response);
                return;
            }
        }
            
        processGetMutationsRequest(request, response);
    }
    
    private static int DEFAULT_THERSHOLD_NUM_SMGS = 500; // no limit if 0 or below
    private void processGetSmgRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String mutationProfileId = request.getParameter(PatientView.MUTATION_PROFILE);
        GeneticProfile mutationProfile;
        Map<Long, Double> mutsig = Collections.emptyMap();
        Map<Long, Map<String, String>> smgs = Collections.emptyMap();
        DaoGeneOptimized daoGeneOptimized = DaoGeneOptimized.getInstance();
        try {
            mutationProfile = DaoGeneticProfile.getGeneticProfileByStableId(mutationProfileId);
            if (mutationProfile!=null) {
                int profileId = mutationProfile.getGeneticProfileId();
                List<Integer> selectedCaseList = new ArrayList<Integer>();
                
                if (request.getParameterMap().containsKey("case_list")) {
                    String caseList = request.getParameter("case_list");
                    Pattern p = Pattern.compile("[,\\s]+");
                    String sampleIds[] = p.split(caseList);
                    CancerStudy cancerStudy = DaoCancerStudy.getCancerStudyByInternalId(mutationProfile.getCancerStudyId());
                    selectedCaseList = InternalIdUtil.getInternalSampleIds(cancerStudy.getInternalId(), Arrays.asList(sampleIds));
                }
                
                // get all recurrently mutation genes
                smgs = mutationModelConverter.convertSignificantlyMutatedGeneToMap(
                        mutationRepository.getSignificantlyMutatedGenes(profileId, null, selectedCaseList, 2, DEFAULT_THERSHOLD_NUM_SMGS));

                // get all cbio cancer genes
                Set<Long> cbioCancerGeneIds = daoGeneOptimized.getEntrezGeneIds(
                        daoGeneOptimized.getCbioCancerGenes());
                cbioCancerGeneIds.removeAll(smgs.keySet());
                if (!cbioCancerGeneIds.isEmpty()) {
                    List<Integer> intEntrezGeneIds = new ArrayList<>(cbioCancerGeneIds.size());
                    for (Long entrezGeneId : cbioCancerGeneIds) {
                        intEntrezGeneIds.add(entrezGeneId.intValue());
                    }
                    smgs.putAll(mutationModelConverter.convertSignificantlyMutatedGeneToMap(
                            mutationRepository.getSignificantlyMutatedGenes(profileId, intEntrezGeneIds, selectedCaseList, -1, -1)));
                }

                // added mutsig results
                mutsig = getMutSig(mutationProfile.getCancerStudyId());
                if (!mutsig.isEmpty()) {
                    Set<Long> mutsigGenes = new HashSet<Long>(mutsig.keySet());
                    mutsigGenes.removeAll(smgs.keySet());
                    if (!mutsigGenes.isEmpty()) {
                        // append mutsig genes
                        List<Integer> intEntrezGeneIds = new ArrayList<>(mutsigGenes.size());
                        for (Long entrezGeneId : mutsigGenes) {
                            intEntrezGeneIds.add(entrezGeneId.intValue());
                        }
                        smgs.putAll(mutationModelConverter.convertSignificantlyMutatedGeneToMap(
                                mutationRepository.getSignificantlyMutatedGenes(profileId, intEntrezGeneIds, selectedCaseList, -1, -1)));
                    }
                }
            }
        } catch (DaoException ex) {
            throw new ServletException(ex);
        }
        
        List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
        for (Map.Entry<Long, Map<String, String>> entry : smgs.entrySet()) {
            Map<String,Object> map = new HashMap<String,Object>();
            
            Long entrez = entry.getKey();
            CanonicalGene gene = daoGeneOptimized.getGene(entrez);
            
            String hugo = gene.getHugoGeneSymbolAllCaps();
            map.put("gene_symbol", hugo);
            
            String cytoband = gene.getCytoband();
            map.put("cytoband", cytoband);
            
            int length = gene.getLength();
            if (length>0) {
                map.put("length", length);
            }
            
            Integer count = Integer.parseInt(entry.getValue().get("count"));
            map.put("num_muts", count);
            
            Double qvalue = mutsig.get(entrez);
            if (qvalue!=null) {
                map.put("qval", qvalue);
            }
            
            Pattern p = Pattern.compile("[,\\s]+");
            String sampleIds[] = p.split(entry.getValue().get("caseIds"));
            List<Integer> sampleInternalIds = new ArrayList<>();
            for(String s : sampleIds) {
                sampleInternalIds.add(Integer.valueOf(s));
            }
            List<String> sampleStableIds = InternalIdUtil.getStableSampleIds(sampleInternalIds);
                
            map.put("caseIds", sampleStableIds);
            data.add(map);
        }
        
        response.setContentType("application/json");
        
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            out.write(mapper.writeValueAsString(data));
        } finally {            
            out.close();
        }
    }

    private void processGetMutationsRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String[] samples = request.getParameter(PatientView.SAMPLE_ID).split(" +");
        String mutationProfileId = request.getParameter(PatientView.MUTATION_PROFILE);
        String mrnaProfileId = request.getParameter(PatientView.MRNA_PROFILE);
        String cnaProfileId = request.getParameter(PatientView.CNA_PROFILE);
        String drugType = request.getParameter(PatientView.DRUG_TYPE);
        boolean fdaOnly = false;
        boolean cancerDrug = true;
        if (drugType!=null && drugType.equalsIgnoreCase(PatientView.DRUG_TYPE_FDA_ONLY)) {
            fdaOnly = true;
            cancerDrug = false;
        }
        
        GeneticProfile mutationProfile;
        List<ExtendedMutation> mutations = Collections.emptyList();
        CancerStudy cancerStudy = null;
        Map<Long, Set<CosmicMutationFrequency>> cosmic = Collections.emptyMap();
        Map<Long, Set<String>> drugs = Collections.emptyMap();
        Map<String, Integer> geneContextMap = Collections.emptyMap();
        Map<String, Integer> keywordContextMap = Collections.emptyMap();
        DaoGeneOptimized daoGeneOptimized = null;
        Map<Long, Map<String,Object>> mrnaContext = Collections.emptyMap();
        Map<Long, String> cnaContext = Collections.emptyMap();
        
        try {
            mutationProfile = DaoGeneticProfile.getGeneticProfileByStableId(mutationProfileId);
            if (mutationProfile!=null) {
                cancerStudy = DaoCancerStudy.getCancerStudyByInternalId(mutationProfile.getCancerStudyId());
                mutations = mutationModelConverter.convert(mutationRepository.getMutations(
                        InternalIdUtil.getInternalSampleIds(cancerStudy.getInternalId(), Arrays.asList(samples)),
                        mutationProfile.getGeneticProfileId()));

                cosmic = DaoCosmicData.getCosmicForMutationEvents(mutations);
                String concatEventIds = getConcatEventIds(mutations);
                int profileId = mutationProfile.getGeneticProfileId();
                daoGeneOptimized = DaoGeneOptimized.getInstance();
                drugs = getDrugs(concatEventIds, profileId, fdaOnly, cancerDrug);
                geneContextMap = getGeneContextMap(concatEventIds, profileId, daoGeneOptimized);
                keywordContextMap = getKeywordContextMap(concatEventIds, profileId);
                Sample sample = (samples.length == 1) ?
                    DaoSample.getSampleByCancerStudyAndSampleId(cancerStudy.getInternalId(), samples[0]) : null;
                if (mrnaProfileId != null && sample != null) { // only if there is only one tumor
                    mrnaContext = getMrnaContext(sample, mutations, mrnaProfileId);
                }
                if (cnaProfileId!=null && samples.length==1) { // only if there is only one tumor
                    cnaContext = getCnaContext(sample, mutations, cnaProfileId);
                }
            }
        } catch (DaoException ex) {
            throw new ServletException(ex);
        }
        
        Map<String,List> data = initMap();
        Map<Long, Integer> mapMutationEventIndex = new HashMap<Long, Integer>();
        for (ExtendedMutation mutation : mutations) {
            List<String> mcgLinks;
            Boolean isHotspot;
            if (mutation.getMutationType().equalsIgnoreCase("Fusion")) {
                mcgLinks = MyCancerGenomeLinkUtil.getMyCancerGenomeLinks(mutation.getGeneSymbol(), "fusion", false);
            } else {
                mcgLinks = MyCancerGenomeLinkUtil.getMyCancerGenomeLinks(mutation.getGeneSymbol(), mutation.getProteinChange(), false);
            }
            isHotspot = OncokbHotspotUtil.getOncokbHotspot(mutation.getGeneSymbol(), mutation.getProteinChange());
            exportMutation(data, mapMutationEventIndex, mutation, cancerStudy,
                    drugs.get(mutation.getEntrezGeneId()), geneContextMap.get(mutation.getGeneSymbol()),
                    mutation.getKeyword()==null?1:keywordContextMap.get(mutation.getKeyword()),
                    cosmic.get(mutation.getMutationEventId()),
                    mrnaContext.get(mutation.getEntrezGeneId()),
                    cnaContext.get(mutation.getEntrezGeneId()),
                    mcgLinks,
                    isHotspot,
                    daoGeneOptimized);
        }

        response.setContentType("application/json");
        
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            out.write(mapper.writeValueAsString(data));
        } finally {            
            out.close();
        }
    }
    
    private void processGetMutationContextRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String mutationProfileId = request.getParameter(PatientView.MUTATION_PROFILE);
        String eventIds = request.getParameter(MUTATION_EVENT_ID);
        
        GeneticProfile mutationProfile;
        Map<String, Integer> geneContextMap = Collections.emptyMap();
        Map<String, Integer> keywordContextMap = Collections.emptyMap();
//        Map<Long, Integer> mutationContextMap = Collections.emptyMap();
        
        try {
            mutationProfile = DaoGeneticProfile.getGeneticProfileByStableId(mutationProfileId);
            if (mutationProfile!=null) {
                DaoGeneOptimized daoGeneOptimized = DaoGeneOptimized.getInstance();
                geneContextMap = getGeneContextMap(eventIds, mutationProfile.getGeneticProfileId(), daoGeneOptimized);
                keywordContextMap = getKeywordContextMap(eventIds, mutationProfile.getGeneticProfileId());
//                mutationContextMap = DaoMutation.countSamplesWithMutationEvents(
//                        eventIds, mutationProfile.getGeneticProfileId());
            }
        } catch (DaoException ex) {
            throw new ServletException(ex);
        }
        
        Map<String, Map<?, Integer>> map = new HashMap<String, Map<?, Integer>>();
        map.put(GENE_CONTEXT, geneContextMap);
        map.put(KEYWORD_CONTEXT, keywordContextMap);
//        map.put(MUTATION_CONTEXT, mutationContextMap);

        response.setContentType("application/json");
        
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            out.write(mapper.writeValueAsString(map));
        } finally {            
            out.close();
        }
    }
    
    private void processCountMutationsRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String mutationProfileId = request.getParameter(PatientView.MUTATION_PROFILE);
        String strSampleIds = request.getParameter(QueryBuilder.CASE_IDS);
        List<Integer> sampleIds = null;

        GeneticProfile mutationProfile;
        Map<String, Integer> count = Collections.emptyMap();

        mutationProfile = DaoGeneticProfile.getGeneticProfileByStableId(mutationProfileId);
        if (strSampleIds!=null) {
            List<String> stableSampleIds = Arrays.asList(strSampleIds.split("[ ,]+"));
            sampleIds = InternalIdUtil.getInternalNonNormalSampleIds(mutationProfile.getCancerStudyId(), stableSampleIds);
        }
        if (mutationProfile!=null) {
            count = convertMapSampleKeys(mutationModelConverter.convertMutationCountToMap(
                    mutationRepository.countMutationEvents(mutationProfile.getGeneticProfileId(), sampleIds)));
        }

        response.setContentType("application/json");
        
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            out.write(mapper.writeValueAsString(count));
        } finally {            
            out.close();
        }
    }

    private Map<String, Integer> convertMapSampleKeys(Map<Integer, Integer> mutationEventCounts)
    {
        Map<String, Integer> toReturn = new HashMap<String, Integer>();
        for (Integer sampleId : mutationEventCounts.keySet()) {
            Sample s = DaoSample.getSampleById(sampleId);
            toReturn.put(s.getStableId(), mutationEventCounts.get(sampleId));
        }
        return toReturn;
    }
    
    private String getConcatEventIds(List<ExtendedMutation> mutations) {
        if (mutations.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (ExtendedMutation mut : mutations) {
            sb.append(mut.getMutationEventId()).append(',');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    
    private Map<Long, Set<String>> getDrugs(String eventIds, int profileId, boolean fdaOnly,
            boolean cancerDrug)
            throws DaoException {
        DaoDrugInteraction daoDrugInteraction = DaoDrugInteraction.getInstance();
        List<Integer> intEventIds = new ArrayList<>();
        for (String eventId : eventIds.split(",")) {
            intEventIds.add(Integer.parseInt(eventId));
        }

        List<Integer> result = mutationRepository.getGenesOfMutations(intEventIds);

        Set<Long> genes = new HashSet<>();
        for (Integer eventId : result) {
            genes.add(eventId.longValue());
        }
        
        // Temporary way of handling cases such as akt inhibitor for pten loss
        Map<Long,Set<Long>> mapTargetToEventGenes = new HashMap<Long,Set<Long>>();
        Set<Long> moreTargets = new HashSet<Long>();
        for (long gene : genes) {
            Set<Long> targets = daoDrugInteraction.getMoreTargets(gene, "MUT");
            moreTargets.addAll(targets);
            for (Long target : targets) {
                Set<Long> eventGenes = mapTargetToEventGenes.get(target);
                if (eventGenes==null) {
                    eventGenes = new HashSet<Long>();
                    mapTargetToEventGenes.put(target, eventGenes);
                }
                eventGenes.add(gene);
            }
        }
        genes.addAll(moreTargets);
        // end Temporary way of handling cases such as akt inhibitor for pten loss
        
        Map<Long, List<String>> map = daoDrugInteraction.getDrugs(genes,fdaOnly,cancerDrug);
        Map<Long, Set<String>> ret = new HashMap<Long, Set<String>>(map.size());
        for (Map.Entry<Long, List<String>> entry : map.entrySet()) {
            ret.put(entry.getKey(), new HashSet<String>(entry.getValue()));
        }
        
        // Temporary way of handling cases such as akt inhibitor for pten loss
        for (Map.Entry<Long, List<String>> entry : map.entrySet()) {
            Set<Long> eventGenes = mapTargetToEventGenes.get(entry.getKey());
            if (eventGenes!=null) {
                for (long eventGene : eventGenes) {
                    Set<String> drugs = ret.get(eventGene);
                    if (drugs==null) {
                        drugs = new HashSet<String>();
                        ret.put(eventGene, drugs);
                    }
                    drugs.addAll(entry.getValue());
                }
            }
        }
        // end Temporary way of handling cases such as akt inhibitor for pten loss
        
        return ret;
    }
    
    private Map<Long, String> getCnaContext(Sample sample, List<ExtendedMutation> mutations,
            String cnaProfileId) throws DaoException {
        Map<Long, String> mapGeneCna = new HashMap<Long, String>();
        DaoGeneticAlteration daoGeneticAlteration = DaoGeneticAlteration.getInstance();
        for (ExtendedMutation mutEvent : mutations) {
            long gene = mutEvent.getEntrezGeneId();
            if (mapGeneCna.containsKey(gene)) {
                continue;
            }
            
            String cna = daoGeneticAlteration.getGeneticAlteration(
                    DaoGeneticProfile.getGeneticProfileByStableId(cnaProfileId).getGeneticProfileId(),
                    sample.getInternalId(), gene);
            
            mapGeneCna.put(gene, cna);
        }
        
        return mapGeneCna;
    }
    
    private Map<Long, Map<String,Object>> getMrnaContext(Sample sample, List<ExtendedMutation> mutations,
            String mrnaProfileId) throws DaoException {
        Map<Long, Map<String,Object>> mapGenePercentile = new HashMap<Long, Map<String,Object>>();
        DaoGeneticAlteration daoGeneticAlteration = DaoGeneticAlteration.getInstance();
        for (ExtendedMutation mutEvent : mutations) {
            long gene = mutEvent.getEntrezGeneId();
            if (mapGenePercentile.containsKey(gene)) {
                continue;
            }
            
            Map<Integer,String> mrnaMap = daoGeneticAlteration.getGeneticAlterationMap(
                    DaoGeneticProfile.getGeneticProfileByStableId(mrnaProfileId).getGeneticProfileId(),
                    gene);
            double mrnaCase = parseNumber(mrnaMap.get(sample.getInternalId()));
            if (Double.isNaN(mrnaCase)) {
                continue;
            }
            
            Map<String,Object> map = new HashMap<String,Object>();
            mapGenePercentile.put(gene, map);
            
            map.put("zscore", mrnaCase);
            
            int total = 0, below = 0;
            for (String strMrna : mrnaMap.values()) {
                double mrna = parseNumber(strMrna);
                if (Double.isNaN(mrna)) {
                    continue;
                }
                
                total++;
                if (mrna <= mrnaCase) {
                    below++;
                }
            }
            
            map.put("perc", 100*below/total);
        }
        
        return mapGenePercentile;
    }
    
    private double parseNumber(String mrna) {
        try {
            return Double.parseDouble(mrna);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    
    private Map<String, Integer> getGeneContextMap(String eventIds, int profileId, DaoGeneOptimized daoGeneOptimized)
            throws DaoException {
        List<Integer> intEventIds = new ArrayList<>();
        for (String eventId : eventIds.split(",")) {
            intEventIds.add(Integer.parseInt(eventId));
        }
        List<Integer> genes = mutationRepository.getGenesOfMutations(intEventIds);

        Map<Long, Integer> map = mutationModelConverter.convertMutatedGeneSampleCountToMap(
                mutationRepository.countSamplesWithMutatedGenes(profileId, genes));
        Map<String, Integer> ret = new HashMap<String, Integer>(map.size());
        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            ret.put(daoGeneOptimized.getGene(entry.getKey())
                    .getHugoGeneSymbolAllCaps(), entry.getValue());
        }
        return ret;
    }
    
    private Map<String, Integer> getKeywordContextMap(String eventIds, int profileId)
            throws DaoException {
        List<Integer> intEventIds = new ArrayList<>();
        for (String eventId : eventIds.split(",")) {
            intEventIds.add(Integer.parseInt(eventId));
        }
        Set<String> genes = new HashSet<>(mutationRepository.getKeywordsOfMutations(intEventIds));
        return mutationModelConverter.convertKeywordSampleCountToMap(
                mutationRepository.countSamplesWithKeywords(profileId, new ArrayList<>(genes)));
    }
    
    private Map<String,List> initMap() {
        Map<String,List> map = new HashMap<String,List>();
        map.put("id", new ArrayList());
        map.put("caseIds", new ArrayList());
        map.put("key", new ArrayList());
        map.put("chr", new ArrayList());
        map.put("start", new ArrayList());
        map.put("end", new ArrayList());
        map.put("entrez", new ArrayList());
        map.put("gene", new ArrayList());
        map.put("protein-start", new ArrayList());
        map.put("protein-end", new ArrayList());
        map.put("aa", new ArrayList());
        map.put("aa-orig", new ArrayList());
        map.put("ref", new ArrayList());
        map.put("var", new ArrayList());
        map.put("type", new ArrayList());
        map.put("status", new ArrayList());
        map.put("cosmic", new ArrayList());
        map.put("mutsig", new ArrayList());
        map.put("genemutrate", new ArrayList());
        map.put("keymutrate", new ArrayList());
        map.put("cna", new ArrayList());
        map.put("mrna", new ArrayList());
        map.put("sanger", new ArrayList());
        map.put("cancer-gene", new ArrayList());
        map.put("drug", new ArrayList());
        map.put("ma", new ArrayList());
        map.put("alt-count", new ArrayList());
        map.put("ref-count", new ArrayList());
        map.put("normal-alt-count", new ArrayList());
        map.put("normal-ref-count", new ArrayList());
        map.put("validation", new ArrayList());
        map.put("mycancergenome", new ArrayList());
        map.put("is-hotspot", new ArrayList());
        map.put("dip-log-r", new ArrayList());
        map.put("seg-mean", new ArrayList());
        map.put("cellular-fraction", new ArrayList());
        map.put("total-copy-number", new ArrayList());
        map.put("minor-copy-number", new ArrayList());
        map.put("purity", new ArrayList());
        map.put("ploidy", new ArrayList());
        map.put("ccf-m-copies", new ArrayList());
        map.put("ccf-m-copies-lower", new ArrayList());
        map.put("ccf-m-copies-upper", new ArrayList());
        map.put("ccf-m-copies-prob95", new ArrayList());
        map.put("ccf-m-copies-prob90", new ArrayList());
        map.put("ccf-1-copy", new ArrayList());
        map.put("ccf-1-copy-lower", new ArrayList());
        map.put("ccf-1-copy-upper", new ArrayList());
        map.put("ccf-1-copy-prob95", new ArrayList());
        map.put("ccf-1-copy-prob90", new ArrayList());
        
        return map;
    }
    
    private Map<String,Integer> addSampleCountMap(Map<String,Integer> map, String sampleId, int num) {
        if (num>=0) {
            map.put(sampleId, num);
        }
        return map;
    }

    private Map<String,Float> addSampleFloatMap(Map<String,Float> map, String sampleId, float num) {	
        map.put(sampleId, num);
        return map;
    }

    private Map<String,Integer> addSampleIntegerMap(Map<String,Integer> map, String sampleId, int num) {	
        map.put(sampleId, num);
        return map;
    }
    
    private void exportMutation(Map<String,List> data, Map<Long, Integer> mapMutationEventIndex,
            ExtendedMutation mutation, CancerStudy cancerStudy, Set<String> drugs,
            int geneContext, int keywordContext, Set<CosmicMutationFrequency> cosmic, Map<String,Object> mrna,
            String cna, List<String> mycancergenomelinks, Boolean isHotspot, DaoGeneOptimized daoGeneOptimized) throws ServletException {
        Sample sample = DaoSample.getSampleById(mutation.getSampleId());
        Long eventId = mutation.getMutationEventId();
        Integer ix = mapMutationEventIndex.get(eventId);
        if (ix!=null) { // multiple samples
            List.class.cast(data.get("caseIds").get(ix)).add(DaoSample.getSampleById(mutation.getSampleId()).getStableId());
            addSampleCountMap(Map.class.cast(data.get("alt-count").get(ix)),sample.getStableId(), mutation.getTumorAltCount());
            addSampleCountMap(Map.class.cast(data.get("ref-count").get(ix)),sample.getStableId(), mutation.getTumorRefCount());
            addSampleCountMap(Map.class.cast(data.get("normal-alt-count").get(ix)),sample.getStableId(), mutation.getNormalAltCount());
            addSampleCountMap(Map.class.cast(data.get("normal-ref-count").get(ix)),sample.getStableId(), mutation.getNormalRefCount());
			addSampleFloatMap(Map.class.cast(data.get("seg-mean").get(ix)),sample.getStableId(),mutation.getSegMean());
        	addSampleFloatMap(Map.class.cast(data.get("cellular-fraction").get(ix)),sample.getStableId(),mutation.getCellularFraction());
        	addSampleIntegerMap(Map.class.cast(data.get("total-copy-number").get(ix)),sample.getStableId(),mutation.getTotalCopyNumber());
        	addSampleIntegerMap(Map.class.cast(data.get("minor-copy-number").get(ix)),sample.getStableId(),mutation.getMinorCopyNumber());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-m-copies").get(ix)),sample.getStableId(),mutation.getCcfMCopies());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-m-copies-lower").get(ix)),sample.getStableId(),mutation.getCcfMCopiesLower());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-m-copies-upper").get(ix)),sample.getStableId(),mutation.getCcfMCopiesUpper());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-m-copies-prob95").get(ix)),sample.getStableId(),mutation.getCcfMCopiesProb95());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-m-copies-prob90").get(ix)),sample.getStableId(),mutation.getCcfMCopiesProb90());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-1-copy").get(ix)),sample.getStableId(),mutation.getCcf1Copy());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-1-copy-lower").get(ix)),sample.getStableId(),mutation.getCcf1CopyLower());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-1-copy-upper").get(ix)),sample.getStableId(),mutation.getCcf1CopyUpper());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-1-copy-prob95").get(ix)),sample.getStableId(),mutation.getCcf1CopyProb95());
        	addSampleFloatMap(Map.class.cast(data.get("ccf-1-copy-prob90").get(ix)),sample.getStableId(),mutation.getCcf1CopyProb90());
            return;
        }
        
        mapMutationEventIndex.put(eventId, data.get("id").size());
        
        data.get("id").add(mutation.getMutationEventId());
        List<String> samples = new ArrayList<String>();
        samples.add(sample.getStableId());
        data.get("caseIds").add(samples);
        data.get("key").add(mutation.getKeyword());
        data.get("chr").add(mutation.getChr());
        data.get("start").add(mutation.getStartPosition());
        data.get("end").add(mutation.getEndPosition());
        String symbol = mutation.getGeneSymbol();
        data.get("entrez").add(mutation.getEntrezGeneId());
        data.get("gene").add(symbol);
        data.get("protein-start").add(mutation.getOncotatorProteinPosStart());
        data.get("protein-end").add(mutation.getOncotatorProteinPosEnd());
        data.get("aa").add(mutation.getProteinChange());
        data.get("aa-orig").add(mutation.getAminoAcidChange());
        data.get("ref").add(mutation.getReferenceAllele());
        data.get("var").add(mutation.getTumorSeqAllele());
        data.get("type").add(mutation.getMutationType());
        data.get("status").add(mutation.getMutationStatus());
        data.get("alt-count").add(addSampleCountMap(new HashMap<String,Integer>(),sample.getStableId(),mutation.getTumorAltCount()));
        data.get("ref-count").add(addSampleCountMap(new HashMap<String,Integer>(),sample.getStableId(),mutation.getTumorRefCount()));
        data.get("normal-alt-count").add(addSampleCountMap(new HashMap<String,Integer>(),sample.getStableId(),mutation.getNormalAltCount()));
        data.get("normal-ref-count").add(addSampleCountMap(new HashMap<String,Integer>(),sample.getStableId(),mutation.getNormalRefCount()));
        data.get("validation").add(mutation.getValidationStatus());
        data.get("cna").add(cna);
        data.get("mrna").add(mrna);
        data.get("mycancergenome").add(mycancergenomelinks);
        data.get("is-hotspot").add(isHotspot);
		
		// FACETS
        data.get("dip-log-r").add(mutation.getDipLogR());
        data.get("seg-mean").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getSegMean()));
        data.get("cellular-fraction").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCellularFraction()));
        data.get("total-copy-number").add(addSampleIntegerMap(new HashMap<String,Integer>(),sample.getStableId(),mutation.getTotalCopyNumber()));
        data.get("minor-copy-number").add(addSampleIntegerMap(new HashMap<String,Integer>(),sample.getStableId(),mutation.getMinorCopyNumber()));
        data.get("purity").add(mutation.getPurity());
        data.get("ploidy").add(mutation.getPloidy());
        data.get("ccf-m-copies").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcfMCopies()));
        data.get("ccf-m-copies-lower").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcfMCopiesLower()));
        data.get("ccf-m-copies-upper").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcfMCopiesUpper()));
        data.get("ccf-m-copies-prob95").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcfMCopiesProb95()));
        data.get("ccf-m-copies-prob90").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcfMCopiesProb90()));
        data.get("ccf-1-copy").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcf1Copy()));
        data.get("ccf-1-copy-lower").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcf1CopyLower()));
        data.get("ccf-1-copy-upper").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcf1CopyUpper()));
        data.get("ccf-1-copy-prob95").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcf1CopyProb95()));
        data.get("ccf-1-copy-prob90").add(addSampleFloatMap(new HashMap<String,Float>(),sample.getStableId(),mutation.getCcf1CopyProb90()));
        
        // cosmic
        data.get("cosmic").add(convertCosmicDataToMatrix(cosmic));
        
        // mut sig
        Double mutSigQvalue;
        try {
            mutSigQvalue = getMutSigQValue(cancerStudy.getInternalId(),
                    mutation.getEntrezGeneId());
        } catch (DaoException ex) {
            throw new ServletException(ex);
        }
        data.get("mutsig").add(mutSigQvalue);
        
        // context
        data.get("genemutrate").add(geneContext);
        data.get("keymutrate").add(keywordContext);
        
        // sanger & cbio cancer gene
        boolean isSangerGene = false;
        boolean isCbioCancerGene = false;
        try {
            isSangerGene = DaoSangerCensus.getInstance().getCancerGeneSet().containsKey(symbol);
            isCbioCancerGene = daoGeneOptimized.isCbioCancerGene(mutation.getGene());
        } catch (DaoException ex) {
            throw new ServletException(ex);
        }
        data.get("sanger").add(isSangerGene);
        data.get("cancer-gene").add(isCbioCancerGene);
        
        // drug
        data.get("drug").add(drugs);
        
        // mutation assessor
        Map<String,String> ma = new HashMap<String,String>();
        ma.put("score", mutation.getFunctionalImpactScore());
        ma.put("xvia", mutation.getLinkXVar());
        ma.put("pdb", mutation.getLinkPdb());
        ma.put("msa", mutation.getLinkMsa());
        data.get("ma").add(ma);
    }
    
    private List<List> convertCosmicDataToMatrix(Set<CosmicMutationFrequency> cosmic) {
        if (cosmic==null) {
            return null;
        }
        List<List> mat = new ArrayList(cosmic.size());
        for (CosmicMutationFrequency cmf : cosmic) {
            List l = new ArrayList(3);
            l.add(cmf.getId());
            l.add(cmf.getAminoAcidChange());
            l.add(cmf.getFrequency());
            mat.add(l);
        }
        return mat;
    }
    
    private static final Map<Integer,Map<Long,Double>> mutSigMap // map from cancer study id
            = new HashMap<Integer,Map<Long,Double>>();     // to map from gene to Q-value
    
    private static Double getMutSigQValue(int cancerStudyId, long entrez) throws DaoException {
        Map<Long,Double> mapGeneQvalue;
        synchronized(mutSigMap) {
            mapGeneQvalue = mutSigMap.get(cancerStudyId);
            if (mapGeneQvalue == null) {
                mapGeneQvalue = new HashMap<Long,Double>();
                mutSigMap.put(cancerStudyId, mapGeneQvalue);
                for (MutSig ms : DaoMutSig.getAllMutSig(cancerStudyId)) {
                    double qvalue = ms.getqValue();
                    mapGeneQvalue.put(ms.getCanonicalGene().getEntrezGeneId(), qvalue);
                }
            }
        }
        return mapGeneQvalue.get(entrez);
    }
    
    private static Map<Long,Double> getMutSig(int cancerStudyId) throws DaoException {
        Map<Long,Double> mapGeneQvalue;
        synchronized(mutSigMap) {
            mapGeneQvalue = mutSigMap.get(cancerStudyId);
            if (mapGeneQvalue == null) {
                mapGeneQvalue = new HashMap<Long,Double>();
                mutSigMap.put(cancerStudyId, mapGeneQvalue);
                for (MutSig ms : DaoMutSig.getAllMutSig(cancerStudyId)) {
                    double qvalue = ms.getqValue();
                    mapGeneQvalue.put(ms.getCanonicalGene().getEntrezGeneId(),
                            qvalue);
                }
            }
        }
        return mapGeneQvalue;
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
