#!/usr/bin/python

# reftrackgen.py
# $Id: reftrackgen.py 17 2009-07-30 13:13:54Z ChristopherDonahue $
#
# This script is expected to be run from within BSSPS.  It takes in BSS data and a project file as arguments.
# The script finds all BSN obs files within the specified project file [Data Path] and creates reference
# baselines for them. The script will look through the BSS data for all BSSPS project files and ask the user
# which one they want to use to grab a survey record for each BSN obs file. It will also create all survey
# records for every reference site observation file (ending in .**o) in the project file [Data Path].

import optparse as opt
import os
import glob
import sys
import fnmatch
import re
import datetime
from Tkinter import *

##### RADIO BUTTON CLASS #####

class RadioSelect:
	def sel(self):
		self.root.destroy()

	def __init__(self, files, bsnfile):
		self.root = Tk()
		self.var = IntVar()
		self.var.set('-1')
		self.root.title(bsnfile)
		xpos = int(self.root.winfo_screenwidth() / 2) - 100
		ypos = int(self.root.winfo_screenheight() / 2) - 100
		self.root.geometry('-%s+%s' % (xpos, ypos))

		text = 'Select project associated with %s' % (bsnfile)
		self.label = Label(self.root)
		self.label.config(text = text)
		self.label.pack(pady=3, padx=5)
		for i in range(len(files)):
			R = Radiobutton(self.root, text=files[i][1:], variable=self.var, value=i, command=self.sel, indicatoron=0)
			R.pack(anchor=W, pady=1, padx=3)
		self.root.mainloop()

	def value(self):
		return self.var.get()

class ErrorReport:
	def __init__(self, datapath):
		self.list = []
		self.errorloc = datapath
		self.output = open(datapath, 'w')
		if not self.output:
			Exit('Could not write to reftrackerr.out')

	def report(self, message):
		self.list.append(message)

	def showdialogue(self):
		self.output.write('ERRORS ENCOUNTERED BY REFTRACKGEN.PY (%s):\n' % len(self.list))
		if (len(self.list) == 0):
			return
		root = Tk()
		root.title('Errors encountered')
		label = Label(root, fg="red")
		label.config(text = 'Project file was updated but encountered errors.\nSee %s for details' % self.errorloc)
		label.pack(anchor=W, pady=1, padx=3)
		for i in range(len(self.list)):
			self.output.write(self.list[i] + '\n')
		self.output.close()
		root.mainloop()

def Exit(message):
	root = Tk()
	root.title('Fatal error encountered')
	label = Label(root)
	label.config(text = 'Project file was not updated. Encountered the following error during execution:')
	label.pack(anchor=W, pady=1, padx=3)
	text = Label(root, fg="red")
	text.config(text = message)
	text.pack(anchor=W, pady=1, padx=3)
	root.mainloop()
	sys.exit(message)

##### CHRIS DONAHUE'S HELPER METHODS #####

# Walks through specified BSS directory finding project files
def FindAllProjectFiles(datapath, exclude_dir):
	# Recursively walk through datapath to find txt files
	txt_files = []
	for root, dirnames, filenames in os.walk(datapath):
		for filename in fnmatch.filter(filenames, '*.txt'):
			if not root == exclude_dir:
				txt_files.append(os.path.join(root, filename))

	# Go through txt files and check if they are BSSPS project files. Build dictionary
	filedict = {}
	for file in txt_files:
		opened = open(file, 'r')
		if opened and opened.readline()[:11] == '[Data Path]':
			match = re.search('\[Survey Records\](.+)\[Baseline Settings\]', opened.read(), re.S)
			if match:
				BuildDictFromProject(filedict, match.expand(r'\1'), file)
		opened.close()

	return filedict

# Builds a dictionary mapping BSN obs files to a list of pairs of (project file, survey record)
def BuildDictFromProject(filedict, records, projfile):
	for line in records.splitlines():
		bsnobs = re.search('\t(BSN\d\d_\d\d[a-z].\d\d\d)\t', line)
		# Possibly check here to see if filename is in the list of survey records before adding it to dict
		if bsnobs and line[:2] == 'BM':
			filename = bsnobs.expand(r'\1')
			if not filename in filedict:
				filedict[filename] = []
			if '\n' or '\r' in line:
				re.sub('\n', '', line)
				re.sub('\r', '', line)
			filedict[filename].append((projfile, line + '\n'))

# Presents a selector to acquire a survey record
def FindSurveyRecord(bsnfile, proj_files, datapath, errorlog):
	# Find project files with the specified BSN file
	datapath = os.path.abspath(datapath)
	bsnfilename = os.path.basename(bsnfile)
	proj_files_with_bsn = []
	rel_files = []
	if bsnfilename in proj_files:
		for pair in proj_files[bsnfilename]:
			proj_files_with_bsn.append(pair[0])
			rel_files.append(pair[0][len(datapath):])
	else:
		errorlog.report('Found no project files with a survey record for %s. Baseline records were not created for this file.' % (bsnfilename))
		return 'none'

	# Present a selector to choose the apt project file if more than 1
	if len(rel_files) > 1:
		selection = RadioSelect(rel_files, bsnfilename).value()
	# Choose it if only 1
	elif len(rel_files) == 1:
		selection = 0

	# Replace with default option on window close
	if selection == -1:
		selection = 0

	# Grab the actual survey record from the file
	if proj_files[bsnfilename][selection][1]:
		return proj_files[bsnfilename][selection][1]
	else:
		Exit('Dictionary sanity error for %s' % bsnfilename)

# Creates a dictionary with reference site obs filename as key mapped to (starttime, endtime)
def MakeTimeDict(records):
	timedict = {}
	for record in records:
		time = re.search('^.+\t.+\t.+\t(\d+)/(\d+)/(\d+)\t\d\t(\d+):(\d+):(\d+)\t(\d+)/(\d+)/(\d+)\t(\d+):(\d+):(\d+)\t(..\d\d\d\d\d.\.\d\do)\t', record)
		if time:
			starttime = datetime.datetime(int(time.expand(r'\3')), int(time.expand(r'\1')), int(time.expand(r'\2')), int(time.expand(r'\4')), int(time.expand(r'\5')), int(time.expand(r'\6')))
			endtime = datetime.datetime(int(time.expand(r'\9')), int(time.expand(r'\7')), int(time.expand(r'\8')), int(time.expand(r'\10')), int(time.expand(r'\11')), int(time.expand(r'\12')))
			timedict[time.expand(r'\13')] = ((starttime, endtime))
		else:
			Exit('The generated record %s is invalid' % record)
	return timedict

def FindOverlappingFiles(starttime, endtime, minoverlap, timedict):
	overlap = []	
	for key in timedict:
		if Overlaps(timedict[key][0], timedict[key][1], starttime, endtime, minoverlap):
			overlap.append(key)
	return overlap

# Finds the baselines associated with a given track obs
def FindBaseline(timedict, minoverlap, obsfilename, siteid, starttime, endtime, errorlog):
	match = re.search('(\d\d\d)([AB])', siteid)
	if match:
		ID = match.expand(r'\1')
	else:
		Exit('Survey record for BSN obs file %s did not have a valid ID' % (obsfilename))

	DOY = re.search('BSN\d\d_(\d\d)[a-z].(\d\d\d)', obsfilename)
	if DOY:
		obsDOY = DOY.expand(r'\2')
		obsYY = DOY.expand(r'\1')
	else:
		Exit('Obs file %s did not have a valid filename' % obsfilename)

	# Define the list of legal sites by site ID
	if int(ID) <= 254:
		legalsites = ['PR03', '1160']
	else:
		legalsites = ['PR23', '1160']

	baselines = []
	overlap = FindOverlappingFiles(starttime, endtime, minoverlap, timedict)

	for refobs in overlap:
		if refobs[:4] in legalsites:
			baselines.append((refobs, obsfilename))

	for baseline in baselines:
		if baseline[0][:4] in legalsites:
			legalsites.remove(str(baseline[0][:4]))

	for site in legalsites:
		reffilename = site + obsDOY + '0.' + obsYY + 'o'
		if reffilename in timedict:
			errorlog.report('Reference site observation file %s does not overlap with BSN observation file %s. Baseline could not be created.' % (reffilename, obsfilename))	
		else:
			errorlog.report('Missing reference site observation file %s. Baseline could not be created with %s.' % (reffilename, obsfilename))

	return baselines

# Returns true if two time ranges overlap by at least overlaptime. Assumes that start1 < end1 and start2 < end2
def Overlaps(start1, end1, start2, end2, overlaptime):
	# ss = second to start
	if start1 < start2:
		ss = start2
	else:
		ss = start1
	# fe = first to end
	if end1 < end2:
		fe = end1
	else:
		fe = end2

	if ss < fe and (fe - ss).seconds > (overlaptime * 60):
		return True
	return False

# Returns a sort key to sort BSN filenames by DOY then session a-z
def bsnsort(bsnname):
	day = re.search('BSN\d\d_\d\d([a-z]).(\d\d\d)', bsnname)
	if day:
		# Multiply DOY by 26 and subtract by a=25 through z=0 to get unique sortkey
		return ((int(day.expand(r'\2')) * 26) + (ord(day.expand(r'\1')) - 122))
	else:
		# Highest number would be BSN**_**z.366 = 9516 so other should be 9517
		return 9517

# Returns a sort key to sort records by Site ID
def recordsort(record):
	siteID = re.search('BM(\d\d\d)([AB])\t\d+\t', record)
	if siteID:
		# Multiply site ID by 2 and subtract by A=1 through B=0 to get unique sortkey
		return ((int(siteID.expand(r'\1')) * 2) + (ord(siteID.expand(r'\2')) - 66))
	else:
		return 1500

			

##### DON TUCKER'S HELPER METHODS #####

def MakeSurveyRecords(obsfilenames, datapath):
	returnarr = []
	for obs in obsfilenames:
		obsfile = os.path.join(datapath, obs)
		startTime,endTime=FindStartAndEndTimes(obsfile)

		match = re.search('.+(\w\w\w\w)\d\d\d\w.\d\do$', obsfile)
		if match:
			siteID = match.expand(r'\1')
		else:
			Exit('Cannot find Site ID in obs file path %s' % (obsfile))

		AntPhsCentX,AntPhsCentY,AntPhsCentZ = FindAntennaPhaseCenter(options.project_filename,siteID)
		InstHgtVert = FindInstrumentHeightVertical(options.project_filename,siteID)
		startWeek,startSOW=FindWeekAndSOWFromDate(startTime)
		endWeek,endSOW=FindWeekAndSOWFromDate(endTime)
		rec = '%s\tNA\t$\t%s\t1\t%s\t%s\t%s\t%s\tNA\tNA\t%s\t0\tNA\t%s\t%f\t%s\t%f\t3\tNA\tNA\tNA\tFile\t%s\t%s\t%s\tFile\t%s\tOther\n' % (siteID,startTime.strftime("%m/%d/%Y"),startTime.strftime("%H:%M:%S"),endTime.strftime("%m/%d/%Y"),endTime.strftime("%H:%M:%S"),obs,siteID,startWeek,startSOW,endWeek,endSOW,AntPhsCentX,AntPhsCentY,AntPhsCentZ,InstHgtVert)
		returnarr.append(rec)
	return returnarr

def FindStartAndEndTimes(RinexObsfileName):
	times_found = 0
	f=open(RinexObsfileName)
	for line in f:
		if 'TIME OF LAST OBS' in line:
			et = line.split()
			times_found += 1
		elif 'TIME OF FIRST OBS' in line:
			st = line.split()
			times_found += 1
		if times_found == 2:
			break
	f.close()

	if times_found < 2:
		Exit('Could not find start and end times in %s' % (RinexObsfileName))

	startTime = datetime.datetime(int(st[0]),int(st[1]),int(st[2]),int(st[3]),int(st[4]),int(float(st[5])))
	endTime = datetime.datetime(int(et[0]),int(et[1]),int(et[2]),int(et[3]),int(et[4]),int(float(et[5])))

	return startTime,endTime

def FindWeekAndSOWFromDate(date):	
	gps0 = datetime.datetime(1980,1,6)
	dt = date - gps0
	week = int(dt.days/7. + dt.seconds/3600./24./7.)
	frac_week = dt.days/7. + dt.seconds/3600./24./7. - week
	SOW = frac_week*7.0*24.0*3600.0
	return str(week),SOW

def FindAntennaPhaseCenter(project_filename,siteID):
	prepath = os.path.dirname(project_filename)
	addition = 'Project/antennas.cfg'
	path = os.path.normpath(os.path.join(prepath, addition))

	if not os.path.exists(path):
		Exit('Could not locate file %s' % (path))

	f=open(path)
	for line in f:
		if line[0] == '/':
			continue
		else:
			pieces = line.split()
			if pieces[0] == siteID:
				f.close()
				return pieces[1],pieces[2],pieces[3]
	f.close()

	Exit('Could not find height for %s in %s' % (siteID, path))

def FindInstrumentHeightVertical(project_filename,siteID):
	prepath = os.path.dirname(project_filename)
	addition = 'Project/heights.cfg'
	path = os.path.normpath(os.path.join(prepath, addition))

	if not os.path.exists(path):
		Exit('Could not locate file %s' % (path))

	f=open(path)
	for line in f:
		if line[0] == '/':
			continue
		else:
			pieces = line.split()
			if pieces[0] == siteID:
				f.close()
				return pieces[1]
	f.close()

	Exit('Could not find height for %s in %s' % (siteID, path))



##### SCRIPT #####

# Parse input arguments and compose help screen
usage = ('usage: %prog [options]\n\n'
				'Pulls survey records concerning BSN observation files from project files within a BSS directory.\n'
				'Creates baseline pairs from the reference sites to the most southern observation from each day.\n\n'
				'Example: \'python reftrackgen.py -d C:\BSSPS\data\Processed_BSS_data\ -p C:\BSSPS\data\Processed_BSS_data\REF-TRACK\\reftrack.txt\''
				' will find the project files in C:\BSSPS\data\Processed_BSS_data\ to associate with the obs files in the [Data Path] from C:\BSSPS\data\Processed_BSS_data\REF-TRACK\\reftrack.txt')
parser = opt.OptionParser(usage=usage)
required_ops = opt.OptionGroup(parser, 'Required')
required_ops.add_option('-d', '--data', action='store', type='string', dest='bss_data',
		help='Path to highest level directory containing all folders for a BSS run.', metavar='BSSData')
required_ops.add_option('-p', '--project', action='store', type='string', dest='project_filename',
		help='Path of the BSSPS project file for the ref-track benchmarks. Project [Data Path] must point to folder with all relevant obs files.', metavar='ProjectFileName' )
required_ops.add_option('-m', '--minoverlap', action='store', type='int', dest='overlap', default=60,
		help='Minimum overlap in minutes between two observation files required to create a baseline.', metavar='MinOverlap' )
parser.add_option_group(required_ops)
(options, args) = parser.parse_args()

# Check for valid input arguments
if(options.bss_data == None):
	parser.print_help()
	Exit('No data path specified')
if(options.project_filename == None):
	parser.print_help()
	Exit('No project name specified')
if not os.path.isdir(options.bss_data):
	Exit('Could not locate directory %s' % (options.bss_data))
if not os.path.exists(options.project_filename):
	Exit('Could not locate file %s' % options.project_filename)

# Try to open project and pull Data Path
openproject = open(options.project_filename)
if openproject:
	errorlog = ErrorReport(os.path.join(os.path.dirname(options.project_filename), 'Project/reftrackwarn.out'))
	if openproject.readline()[:11] == '[Data Path]':
		datap = re.search('(.+)', openproject.readline())
		if datap:
			obsfiles_path = datap.expand(r'\1')
		else:
			openproject.close()
			Exit('No valid Data Path listed in specified ref-track project file')
	else:
		openproject.close()
		Exit('Cannot pull obs data path from invalid ref-track project file')
else:
	openproject.close()
	Exit('Cannot open specified ref-track project file for reading')
openproject.close()

# Handle invalid Data Path
if not os.path.isdir(obsfiles_path):
	Exit('Could not locate obs file directory %s from specified ref-track project file' % obsfiles_path)

# Find BSN obs files in Data Path
BSN_obs = glob.glob('%s/BSN*.[0-9][0-9][0-9]' % (obsfiles_path))
num_records = len(BSN_obs)
if num_records == 0:
	Exit('No BSN Rinex observation files found in ref-track project path: %s' % obsfiles_path)
print '\nFinding survey records for %s BSN obs files.\n' % (str(num_records))

project_files = FindAllProjectFiles(options.bss_data, os.path.dirname(options.project_filename))

if len(project_files) == 0:
	Exit('Could not find any project files in specified directory: %s' % options.bss_data)

# Find associated survey records
surveyrecords = []
for BSN in sorted(BSN_obs, key=bsnsort):
	# FindSurveyRecord searches project files for an appropriate survey record
	record = FindSurveyRecord(BSN, project_files, options.bss_data, errorlog)
	if record != 'none':
		surveyrecords.append(record)

# Generate all ref site survey records and build the time dictionary from these records
refsite_obsfiles = []
for obsfile in set(glob.glob('%s/*.*o' % (obsfiles_path))):
	refsite_obsfiles.append(os.path.basename(obsfile))
refsite_records = MakeSurveyRecords(refsite_obsfiles, obsfiles_path)
timedict = MakeTimeDict(refsite_records)

# Find baselines
baselinerecords = []
for record in sorted(surveyrecords, key=recordsort):
	time = re.search('^BM(.+)\t.+\t.+\t(\d+)/(\d+)/(\d+)\t\d\t(\d+):(\d+):(\d+)\t(\d+)/(\d+)/(\d+)\t(\d+):(\d+):(\d+)\t(BSN\d\d_\d\d[a-z].\d\d\d)\t', record)
	if time:
		starttime = datetime.datetime(int(time.expand(r'\4')), int(time.expand(r'\2')), int(time.expand(r'\3')), int(time.expand(r'\5')), int(time.expand(r'\6')), int(time.expand(r'\7')))
		endtime = datetime.datetime(int(time.expand(r'\10')), int(time.expand(r'\8')), int(time.expand(r'\9')), int(time.expand(r'\11')), int(time.expand(r'\12')), int(time.expand(r'\13')))
		siteid = time.expand(r'\1')
		obsfilename = time.expand(r'\14')
	else:
		Exit('BSN record %s was not properly formatted' % (record))

	# FindBaseline returns up to 2 tuples, add both
	for baseline in sorted(FindBaseline(timedict, options.overlap, obsfilename, siteid, starttime, endtime, errorlog)):
		baselinerecords.append(baseline)

# Add reference file survey records
for record in refsite_records:
	surveyrecords.append(record)
surveyrecords = sorted(surveyrecords)

# Open project file for writing
print 'Generating project file:\n'

# Write project header
writeout = '[Data Path]\n'
writeout += obsfiles_path
writeout += '\n\n[Current Page]\n0\n\n[Status]\n'


# Add survey records
print 'Adding Survey Records.\n'
writeout += '2\t2\t2\t1\t1\n\n[Survey Records]\n%s\n' % (len(surveyrecords))
for record in sorted(surveyrecords):
	writeout += record

# Add baselines
print 'Adding Baselines.\n'
writeout += '\n[Baseline Settings]\n60\t2\t0\t0\n\n[Baselines]\n'
num_baselines = len(baselinerecords)
writeout += '%s\n' % (str(num_baselines))
count = 1
for baseline in baselinerecords:
	baselineout = baseline[0] + '\t' + baseline[1] + '\tbaseline%s.cfg\t2\n' % (count)
	writeout += baselineout
	count += 1

# Write project footer
writeout += '\n[EOPP Extended]\n1\n\n[Time Offset Settings]\n5\t5\n'

# Write out project
pfile = open(options.project_filename,'w')
if pfile:
	pfile.write(writeout)
else:
	pfile.close()
	Exit('Project file %s could not be written to' % (obsfiles_path))
pfile.close()

# Close project file
print 'Done.'
errorlog.showdialogue()
