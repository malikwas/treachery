import {means, evidences} from './blueAndRedCards.js';
import {writeFileSync, write} from 'fs';
import ImageSearchClient from 'azure-cognitiveservices-imagesearch';
import rest from 'ms-rest-azure';

const serviceKey = '####';
const credentials = new rest.CognitiveServicesCredentials(serviceKey);
const searchClient = new ImageSearchClient(credentials);

const sleep = m => new Promise(r => setTimeout(r, m))

const map = {}
const log = [];

map.means = [];
map.evidences = [];

const procedure = async () => {
  await pushMeans();
  await pushEvidences();
  await output();
};

const pushMeans = async () => {
  const {length} = means;

  for (let i = 0; i < length; i++) {
    const mean = means[i];
    const imageResults = await searchClient.imagesOperations.search(mean);

    if (imageResults === null) {
      log.push(`No results found for mean ${mean}`);
      map.means.push({
        name: mean,
        image: '',
        alternate_image: '',
      });
    } else {
      console.log(i);
      map.means.push({
        name: mean,
        image: imageResults.value[0].contentUrl,
        alternate_image: imageResults.value[1].contentUrl,
      });
    }

    await sleep(1100);
  }
};

const pushEvidences = async () => {
  const {length} = evidences;

  for (let i = 0; i < length; i++) {
    const evidence = evidences[i];
    const imageResults = await searchClient.imagesOperations.search(evidence);

    if (imageResults === null) {
      log.push(`No results found for evidence ${evidence}`);
      map.evidences.push({
        name: evidence,
        image: '',
        alternate_image: '',
      });
    } else {
      console.log(i);
      map.evidences.push({
        name: evidence,
        image: imageResults.value[0].contentUrl,
        alternate_image: imageResults.value[1].contentUrl,
      });
    }

    await sleep(1100);
  }
};

const output = async () => {
  const data = JSON.stringify(map, null, 2);
  writeFileSync("data.json", data);
  writeFileSync("log.txt", log.join('\n'));
};

procedure();