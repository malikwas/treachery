import {means, clues} from './blueAndRedCards.js';
import {writeFileSync, write} from 'fs';
import ImageSearchClient from 'azure-cognitiveservices-imagesearch';
import rest from 'ms-rest-azure';

const serviceKey = '####';
const credentials = new rest.CognitiveServicesCredentials(serviceKey);
const searchClient = new ImageSearchClient(credentials);

const sleep = m => new Promise(r => setTimeout(r, m))

const map = {}
const log = [];

map.meansCards = [];
map.clueCards = [];

const procedure = async () => {
  await pushMeansCards();
  await pushClueCards();
  await output();
};

const pushMeansCards = async () => {
  const {length} = means;

  for (let i = 0; i < length; i++) {
    const mean = means[i];
    const imageResults = await searchClient.imagesOperations.search(mean);

    if (imageResults === null) {
      log.push(`No results found for mean ${mean}`);
      map.meansCards.push({
        name: mean,
        image: '',
        alternate_image: '',
      });
    } else {
      console.log(i);
      map.meansCards.push({
        name: mean,
        image: imageResults.value[0].contentUrl,
        alternate_image: imageResults.value[1].contentUrl,
      });
    }

    await sleep(1100);
  }
};

const pushClueCards = async () => {
  const {length} = clues;

  for (let i = 0; i < length; i++) {
    const clue = clues[i];
    const imageResults = await searchClient.imagesOperations.search(clue);

    if (imageResults === null) {
      log.push(`No results found for clue ${clue}`);
      map.clueCards.push({
        name: clue,
        image: '',
        alternate_image: '',
      });
    } else {
      console.log(i);
      map.clueCards.push({
        name: clue,
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