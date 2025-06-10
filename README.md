# BCeID SOAP Sync 🧼
This repository serves as tool for contacting the BCeID SOAP service for
retrieving data about BCeID members. Its original intended purpose is to parse a
CSV file from CCOF CMS and fill in missing information that wasn't captured in
the original sign-up flow. It may be expanded upon for other use-cases.

## Usage
### Setup
Clojure is a language built to run on the JVM. Aside from JDK you will also need
[Leiningen](https://leiningen.org/) to get the dependencies installed.

1. Clone this repository
2. Ensure you have `JDK21` or better
3. Install [Leiningen](https://leiningen.org/#install)
4. Once `lein` is on your path, at the project root run `lein install` and then
   `lein cloverage` to run tests and check unit test coverage.
5. Developers: check out some suggested [editor plugins](https://clojure.org/guides/editors)
   to make developing with Clojure way more fun.

### CSV Data This tool requires a CSV export of incomplete user data and is
opinionated in how that CSV data is structured. See the [test
data](./resources/input-test.csv) for an example.

### Set Environment Variables This tool requires various environment variables
to be set for the application runtime to work. They are:

- `INPUT_CSV` - Absolute path to the input data
- `ACCOUNT_NAME` - The IDIR service account for looking data up with
- `ACCOUNT_PASSWORD` - The IDIR password for the `ACCOUNT_NAME`
- `ACCOUNT_GUID` - The IDIR service account GUID which is required for SOAP
  envelope calls
- `SERVICE_SITE` - The BCeID services URL to use
- `SERVICE_ID` - The ID for the services endpoint
- `API_PATH` - The path to the API root (e.g. ~path/to/client/version/~).
  **include the trailing slash**
- `ACTION_ROOT` - The API Path for the `SOAPAction` and `xmlns` (XML Namespace)

You may set these variables with a tool such as [direnv](https://direnv.net/).
If you are running NixOS, a shell file is provided for dependency installation.
You need only add ~use flake~ to your ~.envrc~.

### Running the tool From the project root, run `lein run`

## Customization This tool is intended to be re-usable. A SOAP envelope of any
kind may be built be using composition. See
[xml-soap](./src/soap_sync/xml_soap.clj) to see how this can be done. To change
things, you need only extend this program as needed and modify the core entry
point at [core.clj](./src/soap_sync/core.clj).
