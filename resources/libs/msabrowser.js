

    const Alteration = {
        Modification: 'modification',
        Variation: 'variation'
    }

    function MSAProcessor({
        fasta,
        hasConsensus = false
    }) {
        this.sequenceDetails = [];
        this.processedSequences = [];
        this.fasta = fasta;
        this.extractLinkFromId = function(sequenceId) {
            var sequenceType = sequenceId.substring(0, 2);
            var link;
            regexPattern = "[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}";
            if (sequenceType == "gi") { link = "https://www.ncbi.nlm.nih.gov/protein/" + sequenceId.split("|")[3]; } 
            else if (sequenceType == "NP" || sequenceType == "XP") { link = "https://www.ncbi.nlm.nih.gov/protein/" + sequenceId; } 
            else if (sequenceType == "EN") { link = "https://www.ensembl.org/id/" + sequenceId; }
            else if (sequenceId.search(regexPattern) != "-1") { sequenceId = sequenceId.split("|")[1]; link = "https://www.uniprot.org/uniprot/" + sequenceId; }
            else { link = "https://www.ncbi.nlm.nih.gov/search/all/?term=" + sequenceId; }
            
            return link;
        }

        this.loadSeq = function(fasta, topologies) {
            let firstStartPointer, endPointer, currentStartPointer = -1;
            let posSeq=0;

            do {
                firstStartPointer = fasta.indexOf(">", firstStartPointer + 1);
                endPointer = fasta.indexOf("\n", firstStartPointer + 1);
                currentStartPointer = fasta.indexOf(">", firstStartPointer + 1);
                let sequence = fasta.slice(endPointer + 1, currentStartPointer);
                //removing new line characters inside seq1
                sequence = sequence.replace(/\s/g, "");

                this.processedSequences.push(sequence);
                
                var topology = [];
                for (let i = 0; i < this.topologies[posSeq].length; i++) {
                    topology[i] = this.topologies[posSeq][i];
                }


                //Protein Name-Identifier
                let sequenceDescription = fasta.slice(firstStartPointer + 1, endPointer + 1);

                if (sequenceDescription.substring(0, 2) == "gi") {
                    var sequenceId = sequenceDescription.split("|")[3];
                } else {
                    var sequenceId = sequenceDescription.split(" ")[0];
                }
                let species = sequenceDescription.split("[").slice(-1)[0].split("]")[0];
                let speciesByWord = species.split(" ");
                //species = speciesByWord[0][0] + ". " + speciesByWord[1];
                species = sequenceDescription;
                var link = this.extractLinkFromId(sequenceId);
                this.sequenceDetails.push({
                    link: link,
                    species: species,
                    sequenceId: sequenceId,
                    rawProteinName: sequenceDescription,
                    topology: topology
                });

            } while (currentStartPointer != -1);

        }

        // Time To Run
        loadSeq(fasta);

        this.addConsensus = function() {
            let consensus_logo = "";
            var aaCount = this.processedSequences[0].length;
            for (let positionIndex = 0; positionIndex < aaCount; positionIndex++) {
                var position_dict = {};
                for (let sequenceIndex = 0; sequenceIndex < this.processedSequences.length; sequenceIndex++) {
                    var sequence = this.processedSequences[sequenceIndex];
                    var aminoacid = sequence[positionIndex];
                    position_dict[aminoacid] = positionIndex;
                }
                if (Object.keys(position_dict).length == 1) {
                    consensus_logo = consensus_logo.concat(aminoacid);
                } else if (Object.keys(position_dict).length == this.processedSequences.length / 2) {
                    consensus_logo = consensus_logo.concat(':');
                } else if (Object.keys(position_dict).length == this.processedSequences.length) {
                    consensus_logo = consensus_logo.concat('-');
                } else {
                    consensus_logo = consensus_logo.concat('.');
                }
            }
            this.sequenceDetails.push({
                link: '#',
                species: 'Consensus',
                sequenceId: 'Consensus',
                rawProteinName: 'Consensus'
            });
            this.processedSequences.push(consensus_logo);

        }

        if (hasConsensus) {
            this.addConsensus();
        }

        return this;
    };

    function renderMSATemplate({
        ids
    }) {
        return `
        <section class="msa-container">
            <section class="scroll-container">
                <!--Gene name and specie names-->
                <section>
                    <section id="${ids.nameContainer}" class="species-and-gene-names">
                        <div id="${ids.speciesNames}" class="species-names"></div>
                    </section>
                </section>
                <!--MSABrowser | annotations and Sequences Parts -->
                <section id="${ids.annotationSequence}" class="annotation-sequence">
                </section> <!-- end of annotation and sequences parts -->
                <section class="highlighter-container">
                </section>

                <!-- MSABrowser | Protein Sequences -->
                <section id="${ids.sequence}" class="sequence-container">
                    <div id="${ids.aminoacidInfo}" class="aminoacid-info"></div>
                </section> <!-- end of protein sequences -->

            </section>
            <!-- Bottom and fixed panel -->
            <section class="bottom-panel">
                <a href="javascript:void(0)" class="reset-button">Reset</a>
            </section>
            
        </section>
        `;
    }

    function MSABrowser({ // notice the curly braces! we are receiving an object now
        id,
        msa,
        alterations = [],
        annotations = [],
        templateFunction = renderMSATemplate,
        colorSchema
    }) {
        this.colorSchema = colorSchema;
        this.createdDivs = {};

        function i_(name) {
            return `${id}-${name}`;
        }
        this.id = id;
        this.ids = {
            id: id,
            annotationSequence: i_('-annotation-sequence'),
            sequenceLength: i_('-sequence-length'),
            sequence: i_('-sequence'),
            aminoacidInfo: i_('-aminoacid-info'),
            nameContainer: i_('-name-container'),
            geneName: i_('-gene-name'),
            speciesNames: i_('-species-names'),
            positionInput: i_('-position'),
            speciesSelect: i_('-species-select')
        }
        this.msa = msa;
        this.alterationNotes = {};
        this.modificationHighlights = {};
        this.loadedPositions = [];
        for (var seqIndex = 0; seqIndex < msa.processedSequences.length; seqIndex++) {
            this.loadedPositions[seqIndex] = [];
            for (i = 0; i < msa.processedSequences[0].length; i++) {
                this.loadedPositions[seqIndex].push(false);
            }
        }

        var that = this;

        function loadProteins(msa) {
            var ids = that.ids;
            var sequenceLengthforDomain = "width:" + msa.processedSequences[0].length * 20 + "px;";
            document.getElementById(ids.annotationSequence).style = sequenceLengthforDomain;

            for (var i in msa.sequenceDetails) {
                //creating flex container for proteins
                var sequenceDetails = msa.sequenceDetails[i];

                let sequence = document.createElement("section");
                document.getElementById(ids.sequence).appendChild(sequence);
                sequence.id = sequenceDetails.sequenceId;
                sequence.className = "sequence";
                sequence.style = sequenceLengthforDomain;
                var speciesName = document.createElement("div");
                var speciesNameLink = document.createElement("a");
                var sequenceHidingButton = document.createElement("a");
                var speciesTooltip = document.createElement('span');

                sequenceHidingButton.setAttribute("href", "#" + sequenceDetails.sequenceId);
                sequenceHidingButton.setAttribute('class', 'hiding-button');
                speciesNameLink.setAttribute("href", sequenceDetails.link);
                speciesNameLink.setAttribute('target', '_blank');
                speciesTooltip.setAttribute('class', 'tooltiptext');
                speciesTooltip.innerHTML = sequenceDetails.sequenceId;
                speciesName.className = "species-name tooltip";
                sequence.setAttribute("data-id", sequenceDetails.sequenceId);
                speciesName.setAttribute("data-id", sequenceDetails.sequenceId);

                document.getElementById(ids.speciesNames).appendChild(speciesName).appendChild(sequenceHidingButton);
                sequenceHidingButton.appendChild(document.createTextNode('x '));
                document.getElementById(ids.speciesNames).appendChild(speciesName).appendChild(speciesNameLink);
                speciesNameLink.appendChild(document.createTextNode(sequenceDetails.species));
                document.getElementById(ids.speciesNames).appendChild(speciesName).appendChild(speciesTooltip);
            }

            // For hiding sequences
            $(".hiding-button").click(function() {
                var sequenceId = $(this).attr("href").split("#")[1];
                $('[data-id="' + sequenceId + '"]').hide();
            });


        }

        function loadViewportToAANumber(msa) {
            viewportToAANumber = [];

            for (i in msa.processedSequences) {
                sequence = msa.processedSequences[i];
                viewportToAANumber.push([]);
                aa_ind = 0;
                for (ind = 0; ind < sequence.length; ind++) {

                    if (sequence.charAt(ind) == '-') {
                        viewportToAANumber[i].push(-1);
                        continue;
                    } else {
                        viewportToAANumber[i].push(aa_ind);
                        aa_ind++;
                    }
                }
            }
        }
        document.getElementById(id).innerHTML = templateFunction({ ids: this.ids });
        loadProteins(msa);
        this.viewportToAANumber = loadViewportToAANumber(msa);

        this.mainDiv = $('#' + id).find('.msa-container');

        this.loadAminoacidSearch(msa);

        function getOffsetX(prNumber, aaNumber) {
            var indexOfAA = that.getAminoacidPositionInViewport(prNumber, aaNumber);

            var offsetX = indexOfAA * 20 + 30;

            return offsetX;
        }

        this.showAlteration = function(prNumber, aaNumber) {

            let textBox = document.createElement("div");
            let innerTextBox = document.createElement("div");
            textBox.setAttribute("class", "variation-text-box");
            innerTextBox.setAttribute("class", "variation-inner-text-box");

            var sequenceNotes = this.alterationNotes[prNumber];
            for (var source in sequenceNotes[aaNumber]) {
                innerTextBox.innerHTML += "<h3>" + source + "</h3>" + sequenceNotes[aaNumber][source];
            }
            if (innerTextBox.innerHTML == ''){
                return false;
            }
            var aminoacidInfoBox = document.getElementById(this.ids.aminoacidInfo);
            aminoacidInfoBox.innerHTML = '';
            aminoacidInfoBox.appendChild(textBox).appendChild(innerTextBox); // time to insert the textBox into aminoacidInfoBox | eski: aminoacidInfoBox.appendChild(textBox)
            $(".variation-inner-text-box").mouseleave(function(e) {
                var aminoacidInfo = document.getElementById(that.ids.aminoacidInfo);
                aminoacidInfo.innerHTML = "";
            });

            offsetX = getOffsetX(prNumber, aaNumber);
            var container = document.getElementById(this.id).getElementsByClassName("sequence")[0];

            if (container.scrollWidth < (offsetX + 600)) {
                offsetX = offsetX - 340;

                textBox.className += " rightArrow";
            }
            let specificPositionforCVBox = "top: " + (prNumber * 20 - 13) + "px;" + "left: " + (offsetX) + "px;  box-shadow:#555 1px 1px 5px 3px;";
            document.getElementById(this.ids.aminoacidInfo).childNodes[0].style.cssText = specificPositionforCVBox;

        }

        for (i in alterations) {
            variation = alterations[i];
            this.addAlteration(variation);
        }
        this.annotations = {};

        if ((typeof(annotations) != "undefined")) {
            this.addAnnotations(annotations);
        }
        var t;
        var aminoacidContainers = document.getElementById(that.ids.sequence).getElementsByTagName('section');

        function checkScroll() {
            setTimeout(() => {
                if ($('#' + that.ids.sequence).find('section div').length > 50000) {
                    for (var seqIndex in loadedPositions) {
                        for (var i in loadedPositions[seqIndex]) {
                            loadedPositions[seqIndex][i] = false;
                        }
                    }

                    for (section of aminoacidContainers) {
                        section.innerHTML = '';
                    }
                    that.loadDivsInViewport();
                }
            }, 500);
        };

        this.loadDivsInViewport();
        this.mainDiv.find('.scroll-container').scroll(() => {
            clearTimeout(t);
            this.loadDivsInViewport();
            checkScroll();

        });

        this.loadDomainBar();

        $('.reset-button').click(() => { this.reset() });

    }

    MSABrowser.prototype.reset = function() {
        this.mainDiv.find('.sequence, .species-name').show();
    }

    MSABrowser.prototype.loadDomainBar = function() {
        var that = this;
        $('.annotation').each(function() {
            //console.log($(this).data('start-point'), );
            startPosition = that.getAminoacidPositionInViewport(0, parseInt($(this).data('start-point')) - 1);

            width = that.getAminoacidPositionInViewport(0, parseInt($(this).data('end-point')) - 1) - startPosition;

            $(this).css('display', 'flex');
            $(this).css('left', (startPosition * 20) + 'px');
            $(this).width((width * 20) + 'px');
        });

        $(document).on('mouseover', '.specialAa', function() {
            prNumber = $(this).data('sid');
            aaNumber = parseInt($(this).attr('class').split(' ')[0].split('-')[1])
            that.showAlteration(prNumber, viewportToAANumber[prNumber][aaNumber]);
        });
        
        $(document).on('click', '.sequence>div', function() {
            var regex = /i-([0-9]+)/i;
            var sequence = $(this).parent().index('.sequence');
            var position = viewportToAANumber[sequence][$(this).attr('class').match(regex)[1]];

            that.scrollToPosition(sequence+1, position+1);
        })


        var ids = this.ids;
        $('.sequence').width(($('#' + ids.sequenceLength).width()) + 'px');
        $('#' + ids.sequence).width(($('#' + ids.sequenceLength).width()) + 'px');
    }

    MSABrowser.prototype.getAminoacidPositionInViewport = function(species_id, position) {
        var sequence = this.msa.processedSequences[species_id];
        var aminoacidIndex = 0;
        for (i = 0; i < sequence.length; i++) {
            if (sequence.charAt(i) == '-')
                continue;
            if (aminoacidIndex == position) {
                return i;
            }
            if (sequence.charAt(i) != '-') {
                aminoacidIndex++;
            }

        }
        return -1;
    }

    MSABrowser.prototype.highlightPosition = function(species, position) {
        var alignmentPosition = this.getAminoacidPositionInViewport(species, position);

        $mainDiv.find('.highlight-column').removeClass('highlight-column modification-highlighted');
        $mainDiv.find('.position-number').remove();

        template = `<div class="highlight-column position-number" style="left:${alignmentPosition * 20}px">${position+1}</div>`;

        $mainDiv.find('.sequence:eq(0)').append(template);

        $mainDiv.find('.scroll-container').scrollLeft(alignmentPosition * 20 - ($mainDiv.width() - 160) / 2)

        setTimeout(function() {
            $mainDiv.find('.i-' + alignmentPosition).addClass('highlight-column');
            $mainDiv.find('.i-' + alignmentPosition).css('box-sizing', 'unset');
            $mainDiv.find('.sequence:eq(' + species + ') .modification.i-' + alignmentPosition).addClass('modification-highlighted');
        }, 75);
    }
    MSABrowser.prototype.loadAminoacidSearch = function() {
        var ids = this.ids;
        $mainDiv = this.mainDiv;
        containerTemplate = `<section class="go-to-position">
            Search a position: <input type="number" placeholder="3" name="position" min="1" class="form_input" id="${ids.positionInput}">
            Species : <select name="species" id="${ids.speciesSelect}"></select>
            </section>`;

        $mainDiv.find('.bottom-panel').append(containerTemplate);

        for (var i in this.msa.sequenceDetails) {
            var species = this.msa.sequenceDetails[i].species;
            var template = `<option value="${i}">${species}</option>`;
            $('#' + ids.speciesSelect).append(template);
        }


        $(`#${ids.positionInput}, #${ids.speciesSelect}`).on("keyup change", () => {
            var position = parseInt($('#' + this.ids.positionInput).val());
            var species = parseInt($('#' + this.ids.speciesSelect).val());
            this.highlightPosition(species, position - 1)
        });
    }

    MSABrowser.prototype.loadDivsInViewport = function() {

        var ids = this.ids;
        loadedPositions = this.loadedPositions;
        processedSequences = this.msa.processedSequences;
        alterationNotes = this.alterationNotes;
        modificationHighlights = this.modificationHighlights;

        var viewportOffset = document.getElementById(ids.sequence).getBoundingClientRect();
        this.mainDiv.find('.species-and-gene-names').css('left', this.mainDiv.find('.scroll-container').scrollLeft());

        var startX = Math.max(0, parseInt((Math.abs(viewportOffset.left) - document.getElementById(ids.nameContainer).clientWidth) / 20));

        var endX = Math.min(processedSequences[0].length,
            parseInt(startX + (document.getElementById(ids.nameContainer).clientWidth) / 20 + 3 * this.mainDiv.innerWidth() / 40 + 1));

        var startY = 0;
        if (this.mainDiv.find('.gene-name')) {
            startY = -this.mainDiv.find('.gene-name').height() * this.mainDiv.find('.gene-name').length / 20
        }
        startY = Math.max(0, parseInt(startY + $('#' + ids.id).find('.scroll-container').scrollTop() / 20 - 3));

        var endY = Math.min(processedSequences.length, parseInt(startY + 350 / 20 + 1));

        for (var sequenceIndex = startY; sequenceIndex < endY; sequenceIndex++) {
            seq1 = processedSequences[sequenceIndex];
            topology = this.msa.sequenceDetails[sequenceIndex].topology;
            var documentFragment = document.createDocumentFragment();
            for (var positionIndex = startX; positionIndex < endX; positionIndex++) {

                if (loadedPositions[sequenceIndex][positionIndex]) {
                    continue;
                } else {
                    loadedPositions[sequenceIndex][positionIndex] = true;
                }

                if (this.createdDivs[`${sequenceIndex};${positionIndex}`]) {
                    documentFragment.appendChild(this.createdDivs[`${sequenceIndex};${positionIndex}`]);
                    continue;
                }

                let aaBox = document.createElement("div");
                //reading protein sequence letter by letter
                var aaLetter = seq1.charAt(positionIndex).toUpperCase();

                //creating amino acid boxes

                if (aaLetter != '-') {
                    aaBox.className = "i-" + positionIndex;
                }
                if (aaLetter == '-') {
                    continue;
                }

                if (sequenceIndex in alterationNotes && viewportToAANumber[sequenceIndex][positionIndex] != -1 && viewportToAANumber[sequenceIndex][positionIndex] in alterationNotes[sequenceIndex]) {
                    aaBox.className += " specialAa";
                    aaBox.setAttribute('data-sid', sequenceIndex);
                }


                aaBox.innerHTML = aaLetter;
                aaBox.style.cssText = 'left:' + (positionIndex * 20) + 'px;';

                // Color schema for amino acids
                
                var aaColor='white';
                if(positionIndex < topology.length){                     
                    if(topology[positionIndex]=="M"){
                       aaColor='#FFCCCC';
                    }
                }

                //var aaColor = ColorSchemas[this.colorSchema][aaLetter]
                aaBox.style.color += 'black';
                aaBox.style.backgroundColor = aaColor ;
                
                // Special cases for color schema
                //if (aaColor == "#fff" || aaColor == "yellow") { aaBox.style.color += "#555" };
                // Consensus
                if (aaLetter == "." || aaLetter == ":" || !aaColor) { aaBox.style.backgroundColor += "#5c5c5c" };

                documentFragment.appendChild(aaBox);
                this.createdDivs[`${sequenceIndex};${positionIndex}`] = aaBox;
            }
            let element = this.mainDiv[0].getElementsByClassName('sequence')[sequenceIndex];

            element.appendChild(documentFragment);
            documentFragment.innerHTML = '';
        }


    }

    MSABrowser.prototype.renderAnnotationData = function(annotationData){
        var name = annotationData["annotation_id"];
        var link = annotationData["annotation_external_link"];
        var startPoint = annotationData["annotation_start_point"];
        var endPoint = annotationData["annotation_end_point"];
        var repeatCount = Math.max(1, Math.round((endPoint - startPoint) * 20 / 800));
        
        var annotationMessage = `<p style="width: 800px">${name} (${startPoint} - ${endPoint})</p>`.repeat(repeatCount);

        var annotationHtml = `
        <a href="${link}" target="_blank">
            <div class="annotation" data-start-point="${startPoint}" data-end-point="${endPoint}">
                <div class="annotation_start_point">${startPoint}</div>
                ${annotationMessage}
                <div class="annotation_end_point">${endPoint}</div>
            </div>
        </a>
        `;

        return annotationHtml;
    }

    MSABrowser.prototype.addAnnotationContainer = function(geneName){
        ids = this.ids;
        var annotationContainerTemplate = `<section class="sequence-length"></section>`;
        $('#' + ids.annotationSequence).append(annotationContainerTemplate);

        var annotationContainer = $('#' + ids.annotationSequence).find('.sequence-length:last')[0];
        $('#' + ids.speciesNames).before(`<div class="gene-name">`+geneName+`</div>`);
        return annotationContainer;
    }

    MSABrowser.prototype.addAnnotations = function(annotations) {

        var ids = this.ids;

        if (annotations == null || annotations.length == 0) {
            annotations = [{
                source: 'MSA Browser'
            }];
        }

        for (var annotation of annotations) {
            if (this.annotations[annotation.source]) {
                continue;
            }
            this.annotations[annotation.source] = annotations.data;

            var annotationContainer = this.addAnnotationContainer(annotation.source);

            for (var key in annotation.data) {
                annotationHtml = this.renderAnnotationData(annotation.data[key])

                $(annotationContainer).append(annotationHtml);
            };
        }
        
    }

    MSABrowser.prototype.addAlteration = function({
        sequenceIndex,
        position,
        note = "",
        source = "",
        type = Alteration.Variation
    }) {
        var sequenceIndex = sequenceIndex - 1; // the species start from 0
        let aaNumber = position - 1; // the aacids start from 0

        notesByProtein = this.alterationNotes[sequenceIndex];
        if (notesByProtein == undefined) {
            notesByProtein = [];
        }

        if (notesByProtein[aaNumber] == undefined) {
            notesByProtein[aaNumber] = {};
            notesByProtein[aaNumber][source] = "";
        } else if (notesByProtein[aaNumber][source] == undefined) {
            notesByProtein[aaNumber][source] = "";
        }

        notesByProtein[aaNumber][source] += note;
        this.alterationNotes[sequenceIndex] = notesByProtein
        var viewportPosition = this.getAminoacidPositionInViewport(sequenceIndex, aaNumber);
        if (type == Alteration.Modification) {
            if(! this.modificationHighlights[viewportPosition]){
                this.modificationHighlights[viewportPosition] = true;
                var style = `left: ${viewportPosition * 20}px; width: 20px; text-align:center;`;
    
                this.mainDiv.find('.highlighter-container').append(`<div class="modification" style="${style}">*</div>`);    
            } 
        } else {

        }
    }

    function saveAs(uri, filename) {

        var link = document.createElement('a');

        if (typeof link.download === 'string') {

            link.href = uri;
            link.download = filename;

            //Firefox requires the link to be in the body
            document.body.appendChild(link);

            //simulate click
            link.click();

            //remove the link when done
            document.body.removeChild(link);

        } else {

            window.open(uri);

        }
    }

    MSABrowser.prototype.export = function(fileName) {
        if (fileName != "") { var fileName = "MSA_export.fasta" }
        var fileContent = this.msa.fasta;
        var hrefTag = "data:text/plain;charset=UTF-8," + encodeURIComponent(fileContent);
        this.mainDiv.find('.bottom-panel').append('<a class="msa-button export-button" href="' + hrefTag + '" download="' + fileName + '">Download as FASTA</a>');
        this.mainDiv.find('.bottom-panel').append('<a href="javascript:void(0)" class="msa-button ss-button">Save as PNG</a>');

        this.mainDiv.find('.ss-button').click(() => {
            this.mainDiv.find('.ss-button').prepend('<img src="https://cdnjs.cloudflare.com/ajax/libs/fancybox/2.1.5/fancybox_loading.gif" style="height:18px"> ')

            html2canvas(this.mainDiv.find('.scroll-container')[0], { height: this.mainDiv.height(), width: this.mainDiv.width() }).then(canvas => {
                console.log(canvas);
                saveAs(canvas.toDataURL(), 'msa-browser-image.png');
                this.mainDiv.find('.ss-button').find('img').remove();
            });
        })
    }

    MSABrowser.prototype.scrollToPosition = function(species, position) {

        this.highlightPosition(species - 1, position - 1)
        this.mainDiv[0].scrollIntoViewIfNeeded();

        setTimeout(() => { this.showAlteration(species - 1, position - 1); }, 20);
    }
