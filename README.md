<img align="right" width="250" height="47" src="media/Gematik_Logo_Flag.png"/> <br/>

[![Quality Gate Status](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Anotification-builder-library&metric=alert_status&token=7eedf2c73af74fbff2f6659750ace3d4498f0aa7)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Anotification-builder-library) [![Vulnerabilities](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Anotification-builder-library&metric=vulnerabilities&token=7eedf2c73af74fbff2f6659750ace3d4498f0aa7)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Anotification-builder-library) [![Bugs](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Anotification-builder-library&metric=bugs&token=7eedf2c73af74fbff2f6659750ace3d4498f0aa7)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Anotification-builder-library) [![Code Smells](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Anotification-builder-library&metric=code_smells&token=7eedf2c73af74fbff2f6659750ace3d4498f0aa7)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Anotification-builder-library) [![Lines of Code](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Anotification-builder-library&metric=ncloc&token=7eedf2c73af74fbff2f6659750ace3d4498f0aa7)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Anotification-builder-library) [![Coverage](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Anotification-builder-library&metric=coverage&token=7eedf2c73af74fbff2f6659750ace3d4498f0aa7)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Anotification-builder-library)
# Notification-Builder-Library

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
        <a href="#about-the-project">About the project</a>
        <ul>
            <li><a href="#dependency">Dependency</a></li>
            <li><a href="#how-to-use-data-builders">How to use data builders</a></li>
            <li><a href="#content-of-the-standard-message">Content of the standard message</a></li>
        </ul>
	</li>
    <li><a href="#Content-of-the-standard-message">Content of the standard message</a></li>
    <li><a href="#release-notes">Release Notes</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#security-policy">Security Policy</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About the project
This project is used to generate example and test messages in various formats (including FHIR-json, FHIR-xml, HAPI bundle objects).

The library can be used as a CLI tool or as a direct dependency in a Java project.

### Dependency

```xml
<dependency>  
   <groupId>de.gematik.demis</groupId>  
   <artifactId>notification-builder-library</artifactId>  
   <version>X.Y.Z</version>  
</dependency>
```

### How to use data builders
In principle, the individual builders can be used to create Fhir classes. The mandatory fields defined by the DEMIS data model are filled with standard information.

Using the NotifiedPersonDataBuilder as an example, the basic use of the builders will be shown: standardbuilder, what happens with setter, what does a setter need?

`Patient patient = new PotifiedPersonDataBuilder().buildNotifiedPerson();` 

creates a patient resource that contains the data of the standard message as content and sets the necessary content for a data subject.

Setters can be used to set the data subject's fields to specific values. For example, the `setAddress()` method sets the address of the data subject. Both other FHIR resources and primitive data types can be used as values. This is specified by the respective `setter` of the respective `DataBuilder`.

The following example shows the adaptation of the address and name of a data subject:
```java 
NotifiedPersonDataBuilder notifiedPersonDataBuilder = new NotifiedPersonDataBuilder();    
 Address someAddress = new AddressDataBuilder()    
 .setCity("City")   
 .setLine("Street 1")   
 .setPostalCode("12345")   
 .buildAddress();  
 notifiedPersonDataBuilder.setAddress(someAddress);

HumanName humanName = new HumanNameDataBuilder()    
 .setFamilyName("SURNAME") 
 .addGivenName("GIVENNAME")
 .buildHumanName();

notifiedPersoNDataBuilder.setHumanName(humanName);    
 Patient patient = notifiedPersonDataBuilder.buildNotifiedPerson(); 
 ```   

All DataBuilders work according to this principle. To generate a complete notification, the `NotificationLaboratoryBundleDataBuilder` can be used, for example. The respective FHIR resources are transferred to it as content.

The following example shows the setting of the previously created data subject in a notification:
```java 
NotificationBundleLaboratoryDataBuilder notificationBundleLaboratoryDataBuilder =     
        new NotificationBundleLaboratoryDataBuilder();    
 notificationBundleLaboratoryDataBuilder.setNotifiedPerson(notifiedPerson);    
 Bundle bundle = notificationBundleLaboratoryDataBuilder.buildLaboratoryBundle(); 
```

### Content of the standard message

Standard messages are sample messages that can be generated using the test data generator. They are available for the message types "Labormeldung" and "Arztmeldung" (specifically hospitalization message). Both messages refer to Covid19 or Sars Cov 2.

## Release Notes

See [ReleaseNotes](ReleaseNotes.md) for all information regarding the (newest) releases.

## Contributing
If you want to contribute, please check our [CONTRIBUTING.md](.github/CONTRIBUTING.md).

## Security Policy
If you want to see the security policy, please check our [SECURITY.md](.github/SECURITY.md).

## License
EUROPEAN UNION PUBLIC LICENCE v. 1.2

EUPL © the European Union 2007, 2016

Following terms apply:

1. Copyright notice: Each published work result is accompanied by an explicit statement of the license conditions for use. These are regularly typical conditions in connection with open source or free software. Programs described/provided/linked here are free software, unless otherwise stated.

2. Permission notice: Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions::

    1. The copyright notice (Item 1) and the permission notice (Item 2) shall be included in all copies or substantial portions of the Software.

    2. The software is provided "as is" without warranty of any kind, either express or implied, including, but not limited to, the warranties of fitness for a particular purpose, merchantability, and/or non-infringement. The authors or copyright holders shall not be liable in any manner whatsoever for any damages or other claims arising from, out of or in connection with the software or the use or other dealings with the software, whether in an action of contract, tort, or otherwise.

    3. The software is the result of research and development activities, therefore not necessarily quality assured and without the character of a liable product. For this reason, gematik does not provide any support or other user assistance (unless otherwise stated in individual cases and without justification of a legal obligation). Furthermore, there is no claim to further development and adaptation of the results to a more current state of the art.

3. Gematik may remove published results temporarily or permanently from the place of publication at any time without prior notice or justification.

4. Please note: Parts of this code may have been generated using AI-supported technology.’ Please take this into account, especially when troubleshooting, for security analyses and possible adjustments.

See [LICENSE](LICENSE.md).

## Contact
E-Mail to [DEMIS Entwicklung](mailto:demis-entwicklung@gematik.de?subject=[GitHub]%20notification-builder-library)