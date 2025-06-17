<img align="right" width="250" height="47" src="media/Gematik_Logo_Flag.png"/> <br/>    

# Release-Notes notification-builder-library

## Release 6.2.0
* Upgrade HAPI FHIR to 8.2.0
* Fix publishing to Maven Central

## Release 6.1.1
* Fixing metaURL for non Nominal Laboratory reports

## Release 6.1.0
* Introduced further classes for §7.3 disease notifications

## Release 6.0.0
* Introduce copy mechanism for 7.3 disease
* Introduce new utility classes
* Breaking: remove Bundles class, now split into other utility classes

## Release 5.8.0
* Added CustomEvaluationContext to be used by services through NBL library.
* Update dependencies

## Release 5.7.0
* Added Builder for LaboratoryReportNonNominal for §7.3 notifications.
* Updated dependencies

## Release 5.6.1
* Bugfix: reference utils now set resource for `urn:uuid:` references correctly

## Release 5.6.0
* Allow processing resources using only `urn:uuid:` references

## Release 5.5.1
* Updated ospo-resources for adding additional notes and disclaimer

## Release 5.4.0
* Updated OSPO-Guidelines and checks
* Add handling for Maven-Central release

## Release 5.3.0
* Add new builder for multiple bundle and composition profile definitions

## Release 5.2.0
* Fix Profile for NotifiedPersonByNameDataBuilder
* Introduce NotificationCategory enum

## Release 5.1.0
* Adding Reason For Testing in Laboratory Notification
* Updated dependencies

## Release 5.0.0
* Allow multiple speciment to be added to a laboratory notification

## Release 4.0.0
* Updated dependencies
* Updated bundle entry order

## Release 3.6.0
* Update dependencies
* Updated canonicals for IGS
* Allow multiple profile urls

## Release 3.5.0
* Add capability to add submitter details

## Release 3.4.0
* Updated FHIR Parser Library (2.2.0)
* Updated HAPI FHIR to new minor version (7.4.0)

## Release 3.3.0
* Added Laboratory OrderId to LaboratoryReportDataBuilder
* Patches

## Release 3.2.1
* Disease Condition supports multiple notes
* IGS Builder refactored
* Patches

## Release 3.2.0
* Updated FHIR Parser Library (2.1.0)
* Updated HAPI FHIR to new minor version (7.2.0)

## Release 3.1.0
* Method `setDefaults` does not change existing values

## Release 3.0.0
* Added Disease Processing for Gateway

## Release 2.0.0
* Added support for IfSG §6.1

## Release 1.3.1
* Initial GitHub-Release of notification-builder-library

## Internal DEMIS-Releases before the open source deployment on GitHub
# 1.1.0
* add disease notification data builders

# 1.0.10
* fix error in system for identifier of bed occupancy receipt bundle

# 1.0.9
* update snakeyaml dependency

# 1.0.8
* reduce sonar issues, vulnerabilities.
* increasev test code coverage.
* add parser helper for projects as convenient method that have no hapi fhir dependency.

# 1.0.7
* add full urls to receipt bundle entries.
* add content type to binary ressource.

# 1.0.6
* fix house number of RKI organization address

# 1.0.5
* add uuid for reference of binary resource in receipt bundle

# 1.0.4
* fix missing entries in receipt bundle

# 1.0.3
* fix missing pdf binary in receipt bundle

# 1.0.2
* receipt bundle builder set to public

# 1.0.1
* fix some vulnerabilities and remove unecessary dependency

# 1.0.0
* initial release for use in gateway
