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

/** To fetch comments from /data servlet and add the settings and delete-all button depending 
on whether there are 0 or more comments */
const ids = new Array();
function getAndPrintComments(){
  const commentListElement = document.getElementById('comments-container');
  const maxComments = getMaxComments();

  fetch('/data?maxComments=' + maxComments).then((response) => response.json()).then((comments) => {
    if(comments.length==0) {
        getAndSetDeleteAllButton(true);
    }
    else {
        getAndSetDeleteAllButton(false);
    }
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
      ids.push(comment.id);
    })
  });
}

/**Make the settings and delete-all button visible only when there are 1 or more comments */
function getAndSetDeleteAllButton(noCommentsFlag){
    document.getElementById("delete-all-button").addEventListener("click", function(){
        for (const commentId of ids)
            deleteTask(commentId);
    });

    if (noCommentsFlag == false) {
        document.getElementById("delete-all-button").style.display="inline-block";
        document.getElementById("commentSettings").style.display="inline-block";
    }
    else {
        document.getElementById("comments-container").innerHTML="<p>No Comments</p>";
        document.getElementById("delete-all-button").style.display="none";
        document.getElementById("commentSettings").style.display="none";
    }
}

/** Creates an element that represents a task, including its delete button. */
function createCommentElement(comment) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const titleElement = document.createElement('span');
  titleElement.innerText = comment.title;

  const timeElement = document.createElement('p');
  timeElement.className = 'date';
  var date = new Date(comment.timestamp).toLocaleDateString("en-US");
  var time = new Date(comment.timestamp).toLocaleTimeString("en-US"); 
  timeElement.innerHTML = "&#128344;"+date+"&nbsp;"+time;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.className = 'delete';
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteTask(comment.id);
    // Remove the task from the DOM.
    commentElement.remove();
  });

  commentElement.appendChild(timeElement);
  commentElement.appendChild(titleElement);
  commentElement.appendChild(deleteButtonElement);
  return commentElement;
}

/** Tells the server to delete the task. */
function deleteTask(commentId) {
  const params = new URLSearchParams();
  params.append('id', commentId);
  fetch('/delete-comment', {method: 'POST', body: params});
  window.location.replace("index.html");
}

/**Onclicking the commentSettigns button show the dropdown-content */
function commentSettings() {
  document.getElementById("dropdown-content").style.visibility = "visible"; 
  document.getElementById("dropdown-content").style.display = "inline-block"; 
}  

/**Reload along with posting the maxComments to url, when user selects the no. of comments he wants to see. */
function sendForm() {
  window.location.replace("index.html?maxComments="+ document.getElementById('quantity').value );
}

/**Set the default value to maxComments when user doesn't give any input or when the page first loads. */
function getMaxComments() {
    const defaultValue = 15;
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    if(urlParams.get('maxComments') == null)
        return defaultValue;
    else
        return(urlParams.get('maxComments'));
}

/**Makes the form visible and adds the 'action' to it by fetching 
the url(that the form needs to post to) from the servlet */
function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url')
      .then((response) => response.text())
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('upload-images');
        messageForm.action = imageUploadUrl;
        messageForm.classList.remove('hidden');
      });
}

/**Fetching all image urls stored in datastore and presenting them on screen along with date/time and its message */
function getAndShowImages(){
  const imageListElement = document.getElementById('images-container');

  fetch("/my-image-servlet").then((response) => response.json()).then((imageDetails) => {
    if(imageDetails.length==0) {
        imageListElement.innerText = "No images";
    }
    else {
        imageDetails.forEach((imageDetail) => {
            imageListElement.appendChild(createImageElement(imageDetail));
        });
    }
  })
}

/**Creating an image element */
function createImageElement(imageDetail) {
  const imageElement = document.createElement('li');
  imageElement.className = 'image';

  const myImg = document.createElement("IMG");
  myImg.setAttribute("src", imageDetail.imageUrl);
  myImg.setAttribute("width", "250");
  myImg.setAttribute("height", "300");
  myImg.setAttribute("alt", imageDetail.id);

  const myExpandedImg = document.createElement("A");
  myExpandedImg.setAttribute("href", imageDetail.imageUrl);
  myExpandedImg.appendChild(myImg);
        
  const messageElement = document.createElement('p');
  messageElement.innerText = imageDetail.message;
  messageElement.className = 'caption';

  const timeElement = document.createElement('p');
  timeElement.className = 'date';
  const date = new Date(imageDetail.timestamp).toLocaleDateString("en-US");
  const time = new Date(imageDetail.timestamp).toLocaleTimeString("en-US"); 
  timeElement.innerHTML = "&#128344;"+date+"&nbsp;"+time;

  imageElement.appendChild(timeElement);
  imageElement.appendChild(myExpandedImg);
  imageElement.appendChild(messageElement);
  return imageElement;
}
