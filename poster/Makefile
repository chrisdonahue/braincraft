poster=braincraft-poster-2012

$(poster).pdf: $(poster).tex
	pdflatex $(poster)

all:	$(poster).pdf

clean:
	-rm -f *.bbl *.blg *.bbg *.aux $(poster).pdf *.dvi *.out *.log *~
