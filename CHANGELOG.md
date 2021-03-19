# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [5.0.0] - 2021-03-12
### Changed
- Complete redesign of the application

## [4.4.0] - 2020-06-23
### Added
- Concept Association added to staging import

## [4.3.0] - 2020-06-19
### Added
- REST API endpoint for retrieving recommendations for a given search string and optionally a language
- Staging import also accepts JSON now
### Changed
- Error messages for numerical ranges now include the datatype (integer / float)
### Fixed
- Encoding was wrong in German messages file
- Typos in error messages

## [4.2.2] - 2019-08-06
### Fixed
- Return correct definition/designation for root node when getting its children

## [4.2.1] - 2019-08-06
### Fixed
- Getting catalogue nodes was erroneously filtering single subnodes with children

## [4.2.0] - 2019-07-25
### Changed
- Getting filtered catalogues accepts the search term via path param instead of header param
### Deprecated
- Method to get whole catalogues no longer marked as deprecated

## [4.1.0] - 2019-06-26
### Added
- Method to get codes by their value instead of urn

## [4.0.3] - 2019-06-14
### Deprecated
- Method to get whole catalogues marked as deprecated
### Fixed
- Getting allowed subcodes for whole catalogues was very slow for large catalogues

## [4.0.2] - 2019-06-14
### Fixed
- Subcode list was empty

## [4.0.1] - 2019-06-14
### Fixed
- Codes with children that are not permitted in the given value domain are correctly filtered out

## [4.0.0] - 2019-06-13
### Added
- CodeDTO contains "hasSubcodes" attribute (might break compatibility with mdrclient or mdrfaces)

## [3.4.0] - UNRELEASED
### Added
- Accept staged elements in XML format
- Concept association for dataelements
- Optional header param "query" when getting catalogs

## [3.3.0] - 2019-02-08
### Added
- Keycloak compatibility

## [3.2.1] - 2018-01-25
### Added
- more debug output when the MDR scope is missing

## [3.2.0] - 2016-11-25
### Added
- functionality to perform a namespace independent search over the local content of the MDR
### Fixed
- fixed a bug in the abstract handler

## [3.1.0] - 2015-12-21
### Added
- status in the identification object
- identification object to the ResultDTO class


## [3.0.0] - 2015-11-06
### Added
- support for catalogs
- maven site documentation
- REST interface documentation in API Blueprint
### Changed
- **Namespace for the Postgres configuration file changed**
