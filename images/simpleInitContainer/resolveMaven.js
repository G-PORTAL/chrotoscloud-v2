const fetch = require('cross-fetch')
const { XMLParser } = require("fast-xml-parser");
const parser = new XMLParser({ignoreAttributes: true, preserveOrder: true});

module.exports = async function(repository, groupId, artifactId, version, user, password) {
    await new Promise((resolve, reject) => {
        let artifactUrl = repository + '/' + groupId.replaceAll('.', '/') + '/' + artifactId + '/' + version;

        let metadataUrl = artifactUrl + '/maven-metadata.xml';
        console.log('Requesting maven-metadata from\'' + metadataUrl + '\'');
        let promise;
        if (user && password) {
            let headers = new fetch.Headers();
            headers.append('Authorization',
                            'Basic ' + Buffer.from(user + ':' + password).toString('base64'));

            promise = fetch(metadataUrl, {method: 'GET', headers});
        } else {
            promise = fetch(metadataUrl);
        }

        promise.then(response => response.text()).then(xmlContent => {
            console.log('Parsing metadata')
            let pomResponse = parser.parse(xmlContent);

            let versionString = 'core-';
            if (version.endsWith('-SNAPSHOT')) {
                versionString += version.substr(0, version.lastIndexOf('-SNAPSHOT') + 2);
                versionString += pomResponse.metadata.versioning.snapshot.timestamp + '-' +
                    pomResponse.metadata.versioning.snapshot.buildNumber;
            } else {
                versionString += pomResponse.metadata.release;
            }

            versionString += '-all.jar';

            console.log('Version String:', versionString);
            resolve(artifactUrl + '/' + versionString);
        }).catch(error => reject(error));
    });
}