<div style="text-align:right"><img src="media/Gematik_Logo_Flag_small.png" alt="gematik GmbH Logo"/> 
 </div>

# Release-Notes notification-builder-library

## 9.0.4
- fixed immunization occurrence to also handle strings
- fixed notified person address handling to keep address uses
- fixed notified person gender extension handling to keep gender extension

## 9.0.3
- fixed optional questionnaire response excerpt handling 

## 9.0.2
- fix wrongly set id for disease excerpts

## 9.0.1
- fix missing section entry on disease excerpt creation
- update dependencies for org.hl7.fhir.r4, org.hl7.fhir.utilities, commons-io, error_prone_annotations, commons-codec, jacoco-maven-plugin, commons-lang3.

## 9.0.0
- fix laboratory nonnominal bundle deep copy to create excerpts for §6.1, §7.1 and §7.3
- remove deprecated methods from laboratory/pathogen creation builder

## 8.0.0
- Enforcing strict profiles for IGS notifications
- IGS Patient Builder sets extension gender-amtlich when host sex is other

## 7.3.1
- Handling of invalid input data for host birthdate and sex

## 7.3.0
- Bumped dependencies

## 7.2.0
- Added lastUpdate field to Patient and Notification Builders

## 7.1.0
- Updated HAPI FHIR to 8.4.0

## 7.0.0 
- Updated libraries
- 7.3 resources: don't copy current AddressUse Tag and NotifiedPersonFacility
- 7.3 resources: copy symptoms for disease bundles

## 6.3.10
- add relates-to method for NotificationDiseaseDataBuilder

## 6.3.9
- update NotifiedPersonAnonymous Builder with default method
- added setters to NotifiedPersonNomial Builder to be used in child classes
- additional convenient extension addind in Address Builder

## 6.3.8
- fix default title for laboratory anonymous composition resources.

## 6.3.7
- fix for different spellings of titles for notifications according to IfSG

## 6.3.6
- fix issues with copy process of PractitionerRole resources
- fix issues with additional organization resources while copying a notification
- refactoring and fix of code that copies NotifiedPersonFacility resources
- fix missing bundle id in copy result for nonnominal disease notification
- add default title for laboratory and disease composition resources.

## 6.3.5
- fix millisecond data copying of compositions for non nominal laboratory cases
- fix extension handling on deep copy for disease compositions

## 6.3.4
- fixing missing clinicalStatus in deepCopy-method of DiseaseDataBuilder 

## 6.3.3
- fix millisecond data copying of specimen and compositions

## 6.3.2
- add missing version copy in laboratory notification deep copy logic
- add version to notification category input possibility

## 6.3.1
- remove default values for laboratory composition category

## 6.3.0
- Fix missing tag copy function for non nominal disease bundle
- New composition data builder for non nominal compositions
- Remove not used constant

## 6.2.0
- Upgrade HAPI FHIR to 8.2.0
- Fix publishing to Maven Central

## 6.1.1
- Fixing metaURL for non Nominal Laboratory reports

## 6.1.0
- Introduced further classes for §7.3 disease notifications

## 6.0.0
- Introduce copy mechanism for 7.3 disease
- Introduce new utility classes
- Breaking: remove Bundles class, now split into other utility classes

## 5.8.0
- Added CustomEvaluationContext to be used by services through NBL library.
- Update dependencies

## 5.7.0
- Added Builder for LaboratoryReportNonNominal for §7.3 notifications.
- Updated dependencies

## 5.6.1
- Bugfix: reference utils now set resource for `urn:uuid:` references correctly

## 5.6.0
- Allow processing resources using only `urn:uuid:` references

## 5.5.1
- Updated ospo-resources for adding additional notes and disclaimer

## 5.4.0
- Updated OSPO-Guidelines and checks
- Add handling for Maven-Central release

## 5.3.0
- Add new builder for multiple bundle and composition profile definitions

## 5.2.0
- Fix Profile for NotifiedPersonByNameDataBuilder
- Introduce NotificationCategory enum

## 5.1.0
- Adding Reason For Testing in Laboratory Notification
- Updated dependencies

## 5.0.0
- Allow multiple speciment to be added to a laboratory notification

## 4.0.0
- Updated dependencies
- Updated bundle entry order

## 3.6.0
- Update dependencies
- Updated canonicals for IGS
- Allow multiple profile urls

## 3.5.0
- Add capability to add submitter details

## 3.4.0
- Updated FHIR Parser Library (2.2.0)
- Updated HAPI FHIR to new minor version (7.4.0)

## 3.3.0
- Added Laboratory OrderId to LaboratoryReportDataBuilder
- Patches

## 3.2.1
- Disease Condition supports multiple notes
- IGS Builder refactored
- Patches

## 3.2.0
- Updated FHIR Parser Library (2.1.0)
- Updated HAPI FHIR to new minor version (7.2.0)

## 3.1.0
- Method `setDefaults` does not change existing values

## 3.0.0
- Added Disease Processing for Gateway

## 2.0.0
- Added support for IfSG §6.1

## 1.3.1
- Initial GitHub-Release of notification-builder-library

## Internal DEMIS-Releases before the open source deployment on GitHub
# 1.1.0
- add disease notification data builders

# 1.0.10
- fix error in system for identifier of bed occupancy receipt bundle

# 1.0.9
- update snakeyaml dependency

# 1.0.8
- reduce sonar issues, vulnerabilities.
- increasev test code coverage.
- add parser helper for projects as convenient method that have no hapi fhir dependency.

# 1.0.7
- add full urls to receipt bundle entries.
- add content type to binary ressource.

# 1.0.6
- fix house number of RKI organization address

# 1.0.5
- add uuid for reference of binary resource in receipt bundle

# 1.0.4
- fix missing entries in receipt bundle

# 1.0.3
- fix missing pdf binary in receipt bundle

# 1.0.2
- receipt bundle builder set to public

# 1.0.1
- fix some vulnerabilities and remove unecessary dependency

# 1.0.0
- initial for use in gateway
