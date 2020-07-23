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

    //to fetch quote from the servlet and add it to our home page with a heading using ARROW function
    fetch('/random-quote').then((response) => response.text()).then( (quote) => {
        document.getElementById('fun-fact-container').innerText = 
        "\n Fun fact about friends 101: \n" + quote;});

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

function getAndPrintComments() {

    /*fetch('/data').then((response) => response.json()).then((comments) => {
    
    const commentListElement = document.getElementById('comments-container');
    const buttonWithDropdown= "<div class='dropdown'>"+
                    "<p><button class='commentSettings' onclick='commentSettings()'><i></i>&#9881;</button></p>"+
                    "<div id='myDropdown' class='dropdown-content'>"+
                        "<a href='#' onclick='deleteAllComments()'>Delete all comments</a>"+
                        "<a href='#'>Set max no. of comments</a>"+
                    "</div>"+
                "</div>";
    var commentsToBeAdded ="";
    for(const comment of comments) {
        commentsToBeAdded+= "<br>"+comment;
    }
    commentListElement.innerHTML= "<br><p style='font-size: larger;'><strong><em> Comments: </em></strong></p>"+ commentsToBeAdded+ buttonWithDropdown;
  });*/
  const ids = new Array();
  const commentListElement = document.getElementById('comments-container');
  fetch('/data').then(response => response.json()).then((comments) => {
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
      ids.push(comment.id);
    })
  });

  const commentsDivElement = document.getElementById('comments-div');
  //Creating a delete all button
  const deleteAllButtonElement = document.createElement('button');
  deleteAllButtonElement.innerText = 'Delete all comments';
  deleteAllButtonElement.className = 'delete-all-button';
  deleteAllButtonElement.addEventListener('click', () => {
    for (const commentId of ids)
    deleteTask(commentId);
  });
  commentsDivElement.appendChild(deleteAllButtonElement);

  //creating a settings button
  var settingsBtn = document.createElement("BUTTON");
  settingsBtn.innerHTML = "&#9881;";
  settingsBtn.className = "commentSettings";
  commentsDivElement.appendChild(settingsBtn);
}

/** Creates an element that represents a task, including its delete button. */
function createCommentElement(comment) {

  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const titleElement = document.createElement('span');
  titleElement.innerText = comment.title;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.className = 'delete';
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteTask(comment.id);

    // Remove the task from the DOM.
    commentElement.remove();
  });

  commentElement.appendChild(titleElement);
  commentElement.appendChild(deleteButtonElement);
  return commentElement;
}

/** Tells the server to delete the task. */
function deleteTask(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-comment', {method: 'POST', body: params});
}

/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */
function commentSettings() {
  document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.commentSettings')) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}

/** Tells the server to delete the task. */
/*function deleteAllComments(comment) {
  const params = new URLSearchParams();
  params.append('commentId', comment.id);
  console.log(params);
  fetch('/delete-all-comments', {method: 'POST', body: params});
}*/
