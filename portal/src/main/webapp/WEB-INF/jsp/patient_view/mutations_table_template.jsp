<%--
 - Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 -
 - This library is distributed in the hope that it will be useful, but WITHOUT
 - ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 - FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 - is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 - obligations to provide maintenance, support, updates, enhancements or
 - modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 - liable to any party for direct, indirect, special, incidental or
 - consequential damages, including lost profits, arising out of the use of this
 - software and its documentation, even if Memorial Sloan-Kettering Cancer
 - Center has been advised of the possibility of such damage.
 --%>

<%--
 - This file is part of cBioPortal.
 -
 - cBioPortal is free software: you can redistribute it and/or modify
 - it under the terms of the GNU Affero General Public License as
 - published by the Free Software Foundation, either version 3 of the
 - License.
 -
 - This program is distributed in the hope that it will be useful,
 - but WITHOUT ANY WARRANTY; without even the implied warranty of
 - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 - GNU Affero General Public License for more details.
 -
 - You should have received a copy of the GNU Affero General Public License
 - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>


                <thead>
                    <tr valign="bottom">
                        <th>Mutation Event ID</th>
                        <th class="mut-header" alt="Cases/Samples"><b>Tumors</b></th>
                        <th class="mut-header" alt="HUGO symbol of mutated genes"><b>Gene</b></th>
                        <th class="mut-header" alt="Protein change"><b>Protein<br/>Change</b></th>
                        <th class="mut-header" alt=""><b>Annotation</b></th>
                        <th class="mut-header" alt="Chromosome"><b>Chr</b></th>
                        <th class="mut-header" alt="Start postion on chromosome"><b>Start</b></th>
                        <th class="mut-header" alt="End postion on chromosome"><b>End</b></th>
                        <th class="mut-header" alt="Reference Allele"><b>Ref</b></th>
                        <th class="mut-header" alt="Variant Allele"><b>Var</b></th>
                        <th class="mut-header" alt="Validation status"><b>Validation</b></th>
                        <th class="mut-header" alt="Mutation type"><b>Type</b></th>
                        <th class="mut-header" alt="Variant allele frequency in the tumor sample"><b>Allele<br/>Freq</b></th>
                        <th class="mut-header" alt="Variant allele read count in the tumor sample"><b>Variant<br/>Reads</b></th>
                        <th class="mut-header" alt="Reference allele read count in the tumor sample"><b>Ref<br/>Reads</b></th>
                        <th class="mut-header" alt="Variant allele frequency in the normal sample"><b>Allele<br/>Freq (N)</b></th>
                        <th class="mut-header" alt="Variant allele read count in the normal sample"><b>Variant<br/>Reads (N)</b></th>
                        <th class="mut-header" alt="Reference allele read count in the normal sample"><b>Ref<br/>Reads (N)</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction M Copies"><b>FACETS CCF M Copies</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction M Copies Lower"><b>FACETS CCF M Copies Lower</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction M Copies Upper"><b>FACETS CCF M Copies Upper</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction M Copies Prob 95"><b>FACETS CCF M Copies Prob 95</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction M Copies Prob 90"><b>FACETS CCF M Copies Prob 90</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction 1 Copy"><b>FACETS CCF 1 Copy</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction 1 Copy Lower"><b>FACETS CCF 1 Copy Lower</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction 1 Copy Upper"><b>FACETS CCF 1 Copy Upper</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction 1 Copy Prob 95"><b>FACETS CCF 1 Copy Prob 95</b></th>
                        <th class="mut-header" alt="FACETS Cancer Cell Fraction 1 Copy Prob 90"><b>FACETS CCF 1 Copy Prob 90</b></th>
                        <th class="mut-header" alt="FACETS Cell Fraction (Per Segment)"><b>FACETS CF</b></th>
                        <th class="mut-header" alt="Load BAM for this mutation via IGV"><b>BAM</b></th>
                        <th class="mut-header" alt="Copy-number status of the mutated gene"><b>Copy #</b></th>
                        <th class="mut-header" alt="FACETS Copy Number"><b>FACETS Copy #</b></th>
                        <th class="mut-header" alt="FACETS Total Copy Number"><b>FACETS Total Copy #</b></th>
                        <th class="mut-header" alt="FACETS Minor Copy Number"><b>FACETS Minor Copy #</b></th>
                        <th class="mut-header" alt="FACETS Best Guess for Mutant Copies"><b>FACETS Mutant Copies</b></th>
                        <th class="mut-header" alt="Percentile of mRNA expression of the mutated gene in this tumor comparing to the cohort"><b>mRNA Expr.</b></th>
                        <th class="mut-header" alt="Mutation frequency in cohort"><b>Cohort</b></th>
                        <th class="mut-header" alt="Mutation frequency in cBioPortal"><b>cBioPortal</b></th>
                        <th class="mut-header" alt="COSMIC occurrences"><b>COSMIC</b></th>
                        <th class="mut-header" alt="Functional Impact predicted by MutationAssessor (missense mutations only)"><b>Mutation<br/>Assessor</b></th>
                        <th class="mut-header" alt="Potential Drugs"><b>Drugs</b></th>
                        <th class="mut-header" alt="FACETS Dip Log R (Per Sample)"><b>FACETS Dip Log R</b></th>
                        <th class="mut-header" alt="FACETS Seg Mean (Per Segment)"><b>FACETS Seg Mean</b></th>
                        <th class="mut-header" alt="FACETS Purity (Per Sample)"><b>FACETS Purity</b></th>
                        <th class="mut-header" alt="FACETS Ploidy (Per Sample)"><b>FACETS Ploidy</b></th>
                    </tr>
                </thead>
