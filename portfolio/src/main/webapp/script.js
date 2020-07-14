// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addFunFact() {
    const funFacts = ['Jennifer Almost Didn\'t Return For The Final Season',
    'The Cast Took A Trip To Vegas Before The Premiere',
    'They Wanted Courteney Cox To Play Rachel',
    'The Writers Got Creative To Cut Costs',
    'Gunther Was Actually A Barista'];

    const randomFactHeading= '<br> Fun fact about friends 101: <br>';
    const randomFact= funFacts[Math.floor(Math.random()* funFacts.length)];

    const factContainer = document.getElementById('fun-fact-container');
    const factWithHeading = randomFactHeading+randomFact;
    factContainer.innerHTML = factWithHeading;
    
}

function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
