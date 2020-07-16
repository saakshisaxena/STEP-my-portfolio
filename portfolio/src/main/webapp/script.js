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

var clickShowCount= 0;

function showMoreOrLessProjects() {
    clickShowCount++;
    if(clickShowCount%2==0)
    {
        const divToShow= document.getElementById('see-more-projects');
        divToShow.style.visibility= 'hidden';
        divToShow.style.display= 'none';

        const buttonChangeText= document.getElementById('show-projects');
        buttonChangeText.innerHTML= "Show more projects";
    }
    else {
        const divToShow= document.getElementById('see-more-projects');
        divToShow.style.visibility= 'visible';
        divToShow.style.display= 'block';

        const buttonChangeText= document.getElementById('show-projects');
        buttonChangeText.innerHTML= "Show less";
    }
    
}



