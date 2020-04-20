const $body = $("body");
const primaryColor = $("html").css("--primary-color");
const $pagePrompt = $(".page-prompt");

enableCSSAnimations();
showCookieConsentMessage();

function showCookieConsentMessage() {
    if (!localStorage.isCookieConsentMessageShown) {
        showPrompt("Ceno uses cookies to provide the best browsing experience for you");
    }
    localStorage.isCookieConsentMessageShown = false;
}

function enableCSSAnimations() {
    // This should be done after the page is fully loaded;
    // we know that the page is fully loaded because the script is deferred.
    $body.removeClass("preload");
}

function showPrompt(message) {
    // Remove the prompt from page if it already exists
    if ($pagePrompt.length > 0) $pagePrompt.remove();

    let pagePromptHTML =
        `<div class="page-prompt">
             <div class="page-prompt-text">${message}</div>
         </div>`;

    $("main").prepend(pagePromptHTML);
}

//================= Login modal =================\\
var closeBtn = $(".close");
var loginDialog = $("#login-dialog");
var loginWrapper = $("#login-wrapper");
var registerWrapper = $("#register-wrapper");

$("#log-in").on("click touch", () => showModal(loginWrapper, registerWrapper));
$("#sign-up").on("click touch", () => showModal(registerWrapper, loginWrapper));

function showModal(toShow, toHide) {
    toHide.hide();
    loginDialog.show();
    toShow.slideDown(350);
}

$(window).add(closeBtn).click(function (event) {
    if (event.target === loginDialog[0] || event.target === closeBtn[0]) { // Required check
        loginDialog.fadeOut(200, function () {
            registerWrapper.hide();
            loginWrapper.hide();
            emptyInput(usernameInput);
            emptyInput(passwordInput);
            emptyInput(usernameInputLogin);
            emptyInput(passwordInputLogin);
        });
    }
});

function emptyInput(input) {
    input.val("");
    input.siblings(".highlight").hide().css({background: primaryColor});
    input.siblings(".prompt").hide();
}

$(".log-prompt").on("click touch", () => {
    $(".log-form>div").animate({height: "toggle", opacity: "toggle"}, 450);
});

//=========== Add comment to post ===========\\
$(".wrt-comment button").on("click touch", function () {
    var postId = $(this).siblings("input").val();
    var commentField = $(this).siblings("textarea");
    var comment = commentField.val();
    var userName = $(".usr-name").text();
    if (comment.length === 0) {
        return;
    }
    $.post("/posts/comments/add", {postId: postId, comment: comment},
        function () {
            var commCount = $(".comments p");
            commCount.text((_, oldVal) => `Comments (${Number(oldVal.match(/\d+/)) + 1}):`);
            commCount.after(
                `<div class="comment">
                     <div class="comment-author">
                         <img src="/users/avatars/${userName}" alt="${userName}">
                         <div class="com-auth-name">${userName}</div>
                     </div>
                     <div class="comment-text">${comment}</div>
                 </div>`
            );
            commentField.val("");
            $(".comm-count span").text((_, oldVal) => Number(oldVal) + 1);
        });
});

//=========== Category page ajax load ===========\\
var sliceNumber = 1;
var hasNext = true;
if ($(location).attr("pathname").match("^/categories")) {
    var category = $(location).attr("pathname").replace("/categories/", "");
    $(window).scroll(function () {
        if (hasNext && $(window).scrollTop() + $(window).height() > $(document).height() - 50) {
            $.get(`/categories/${category}/slice`, {sliceNumber: sliceNumber}, slice => {
                if (slice === "") {
                    $("#spinner").hide();
                    hasNext = false;
                }
                $(".card:last-child").after(slice);
            });
            sliceNumber++;
        }
    });
}

//========== Avatar preview on Sign up ==========\\
$(".avatar-upload input").on("change", function () {
    if (this.files && this.files[0]) {
        var reader = new FileReader();
        reader.onload = e => $(".avatar-upload img").attr("src", e.target.result);
        reader.readAsDataURL(this.files[0]);
    }
});

$(".post-file input").on("change", function () {
    if (this.files && this.files[0]) {
        var reader = new FileReader();
        reader.onload = e => $(".file-prev").attr("src", e.target.result);
        reader.readAsDataURL(this.files[0]);
    }
});

//==================== Like =====================\\
$body.on("click touch", ".heart", function () {
    var numElement = $(this).siblings(".like-num");
    var postId = $(this).parents(".card,.article-container").attr("data-post-id");
    if ($(this).hasClass("liked")) {
        $(this).toggleClass('liked not-liked');
        $(this).removeClass('animating');
        like(postId, false, numElement, -1);
    } else {
        $(this).addClass('animating');
        like(postId, true, numElement, +1);
    }
});

function like(postId, isLike, numElement, quantity) {
    $.post("/posts/like", {postId: postId, like: isLike},
        function () {
            numElement.text(+numElement.text() + quantity);
        });
}

$body.on('animationend', ".heart", function () {
    $(this).toggleClass('not-liked liked');
});

//============== Side navigation ================\\
$(".hamburger-icon").on("click touch", () => {
    $(".side-nav").css({width: "240px", maxWidth: "100%"});
});

$(".close-cat").on("click touch", () => $(".side-nav").css({width: "0"}));

//================= Share icon ==================\\
$body.on("click touch", ".share-icon", function () {
    var container = $(this).next();
    if (container.css("visibility") === "visible") {
        $(this).css({fill: "#919da5"});
        container.css({
            opacity: "0",
            visibility: "hidden",
            transform: "translate(-20px, 0)"
        });
    } else {
        $(this).css({fill: primaryColor});
        container.css({
            opacity: "1",
            visibility: "visible",
            transform: "translate(-20px, 10px)"
        });
    }
});

$(window).click(function (ev) {
    if (ev.target.tagName !== "svg" && ev.target.tagName !== "path") {
        $(".share-content").each(function () {
            if ($(this).is(":visible")) {
                $(this).siblings(".share-icon").css({fill: "#919da5"});
                $(this).css({
                    opacity: "0",
                    visibility: "hidden",
                    transform: "translate(-20px, 0)"
                });
            }
        });
    }
});

//============== Check existing userName ==================\\
var usernameInput = $(".register-form input[name=username]");
var passwordInput = $(".register-form input[name=password]");
var passRegex = /.*/;
// /((?=.*\d)(?=.*\\p{L}))./;
// /(\d+\p{L}+|\p{L}+\d+)[\d\p{L}]*/;
// /(\d\w*[a-zA-Z]\w*)|([a-zA-Z]\w*\d\w*)/;

$(".flat-inp").focus(function () {
    var content = $(this).val();
    if (content === "") {
        $(this).siblings(".highlight").fadeTo(180, 1);
    }
});

function checkUserName(name, highlight) {
    $.post("/users/check-username", {name: name}, function (exists) {
        if (exists) {
            highlight.css({background: "#de1e26"}).fadeTo(200, 1);
            usernameInput.siblings(".prompt").text("Already taken").fadeTo(200, 1);
        } else {
            highlight.css({background: "#4CAF50"}).fadeTo(200, 1);
            usernameInput.siblings(".prompt").fadeTo(200, 0);
        }
    });
}

usernameInput.blur(function () {
    var name = usernameInput.val();
    var highlight = usernameInput.siblings(".highlight");
    if (name === "") {
        highlight.fadeTo(200, 0, () => highlight.css({background: primaryColor}));
        usernameInput.siblings(".prompt").fadeTo(200, 0);
    } else {
        checkUserName(name, highlight);
    }
});

passwordInput.blur(function () {
    var pass = passwordInput.val();
    var highlight = passwordInput.siblings(".highlight");
    if (pass.length === 0) {
        highlight.fadeTo(180, 0, () => highlight.css({background: primaryColor}));
        passwordInput.siblings(".prompt").fadeTo(200, 0);
    } else if (pass.length < 6 || !passRegex.test(pass)) {
        highlight.css({background: "#de1e26"}).fadeTo(200, 1);
        passwordInput.siblings(".prompt").text(`${pass.length < 6 ? "Too short" : "Invalid"}`).fadeTo(200, 1);
    } else {
        highlight.css({background: "#4CAF50"}).fadeTo(200, 1);
        passwordInput.siblings(".prompt").fadeTo(200, 0);
    }
});

$(".register-form").submit(function () {
    var submit = true;
    var name = usernameInput.val();
    var pass = passwordInput.val();
    var nameHighlight = usernameInput.siblings(".highlight");
    var passHighlight = passwordInput.siblings(".highlight");
    if (name.length === 0) {
        nameHighlight.css({background: "#de1e26"}).fadeTo(200, 1);
        usernameInput.siblings(".prompt").text("Required").fadeTo(200, 1);
        submit = false;
    }
    if (pass.length === 0) {
        passHighlight.css({background: "#de1e26"}).fadeTo(200, 1);
        passwordInput.siblings(".prompt").text("Required").fadeTo(200, 1);
        submit = false;
    } else if (!passRegex.test(pass)) {
        passHighlight.css({background: "#de1e26"}).fadeTo(200, 1);
        passwordInput.siblings(".prompt").text("Invalid").fadeTo(200, 1);
        submit = false;
    }
    return submit;
});

//======== attach csrf header to every AJAX POST request =========\\
var token = $(`meta[name="_csrf"]`).attr("content");
var header = $(`meta[name="_csrf_header"]`).attr("content");
$(document).ajaxSend((e, xhr) => xhr.setRequestHeader(header, token));

//======================== AJAX login ======================
var usernameInputLogin = $(".login-form .input:nth-child(1) input");
var passwordInputLogin = $(".login-form .input:nth-child(2) input");

usernameInputLogin.on("blur", () => {
    var name = usernameInputLogin.val();
    var highlight = usernameInputLogin.siblings(".highlight");
    if (name.length === 0) {
        highlight.fadeTo(200, 0, () => highlight.css({background: primaryColor}));
        usernameInputLogin.siblings(".prompt").fadeTo(200, 0);
    }
});

passwordInputLogin.on("blur", () => {
    var pass = passwordInputLogin.val();
    var highlight = passwordInputLogin.siblings(".highlight");
    if (pass.length === 0) {
        highlight.fadeTo(200, 0, () => highlight.css({background: primaryColor}));
        passwordInputLogin.siblings(".prompt").fadeTo(200, 0);
    }
});

$(".login-form button").on("click touch", function () {
    var submit = true;
    var name = usernameInputLogin.val();
    var pass = passwordInputLogin.val();
    var rememberMe = $("input[name=remember-me]").prop("checked");
    var nameHighlight = usernameInputLogin.siblings(".highlight");
    var passHighlight = passwordInputLogin.siblings(".highlight");
    if (name.length === 0) {
        nameHighlight.css({background: "#de1e26"}).fadeTo(200, 1);
        usernameInputLogin.siblings(".prompt").text("Required").fadeTo(200, 1);
        submit = false;
    }
    if (pass.length === 0) {
        passHighlight.css({background: "#de1e26"}).fadeTo(200, 1);
        passwordInputLogin.siblings(".prompt").text("Required").fadeTo(200, 1);
        submit = false;
    }
    if (submit) {
        $.post("/login", {username: name, password: pass, rememberMe: rememberMe}, successful => {
                if (successful) location.reload(); else $(".invalid-login").fadeTo(200, 1);
            }
        );
    }
});

//========= logout ========\\
$(".logout").on("click touch", function () {
    $(this).parent().submit();
});

$body.on("click touch", ".ply-btn", function () {
    var video = $(this).siblings("video")[0];
    var button = $(this);

    if (video.paused) {
        video.play();
        setTimeout(() => {
            if (!button.is(":hover")) button.fadeTo(200, 0);
        }, 1000);
    } else {
        video.pause();
        $(this).fadeTo(80, 1);
    }
});

$body.on("mouseover", ".ply-btn", function () {
    $(this).fadeTo(60, 1);
});

$body.on("mouseleave", ".ply-btn", function () {
    $(this).fadeTo(60, 1);
    var button = $(this);
    var video = $(this).siblings("video")[0];
    if (!video.paused) {
        setTimeout(() => {
            if (!button.is(":hover")) button.fadeTo(80, 0);
        }, 300);
    }
});

$body.on("click touch", ".ply-btn", function () {
    $(this).find(".ply-ico").toggleClass("ply-ico-active");
});

document.addEventListener("timeupdate", e => { // supports dynamically inserted videos as well
    var video = $(e.target);
    var progressBar = video.siblings("progress");
    var percent = video[0].currentTime / video[0].duration * 100;
    progressBar.stop().animate({value: percent}, (percent === 0) ? 100 : 400);
}, true);

//================= submit post =================\\
$("#post-submit").submit(function () {
    var submit = true;
    $(".input-np").each(function () {
        var input = $(this).find("input, textarea").val();
        if (input.length === 0) {
            var highlight = $(this).find(".highlight-np");
            highlight.css({background: "#de1e26"}).fadeTo(200, 1);
            $(this).find(".prompt").fadeTo(200, 1);
            submit = false;
        }
    });
    return submit;
});

$(".input-np").on("change", function () {
    var input = $(this).find("input, textarea").val();
    if (input.length !== 0) {
        var highlight = $(this).find(".highlight-np");
        highlight.fadeTo(200, 0);
        $(this).find(".prompt").fadeTo(200, 0);
    }
});

//================= pin, delete & report post, change cats ==================\\
// $(".pin-icon").on("click touch", function () {
//     var postId = $(".article-container").attr("data-post-id");
//     var url = window.location.href;
//     $.post("/posts/pin", {postId: postId}, function (successful) {
//         window.location.href = url;
//     });
// });

$(".actions form").submit(function () {
    // proceed only if the icon doesn't have "disabled" class
    // (that is the user is logged in and has an appropriate role)
    return !($(this).find("svg").hasClass("disabled"));
});

// $(".del-icon").on("click touch", function () {
//     var postId = $(".article-container").attr("data-post-id");
//     $.post("/posts/delete", {postId: postId}, function (successful) {
//         if (successful) {
//             //
//         }
//     });
// });

// $(".rep-icon").on("click touch", function () {
//     var postId = $(".article-container").attr("data-post-id");
//     $.post("/posts/report", {postId: postId}, function (successful) {
//         if (successful) {
//             //
//         }
//     });
// });

$(".chng-cats-icon").on("click touch", function () {
    var userName = $(".usr-name").text(); // just to check if user is logged in
    if (!userName) return;

    var container = $(this).next();
    if (container.css("visibility") === "visible") {
        $(this).css({fill: "#919da5"});
        container.css({
            opacity: "0",
            visibility: "hidden",
            transform: "translate(-20px, 0)"
        });
    } else {
        $(this).css({fill: primaryColor});
        container.css({
            opacity: "1",
            visibility: "visible",
            transform: "translate(-20px, 10px)"
        });
    }
});

$(".cat-del-ico").on("click touch", function () {
    var postId = $(".article-container").attr("data-post-id");
    var catName = $(this).siblings(".changeable-cat").text();
    var toRemove = $(this).parent();
    $.post("/categories/delete", {postId: postId, catName: catName}, () => toRemove.remove());
});

$(".cat-add-ico").on("click touch", function () {
    var postId = $(".article-container").attr("data-post-id");
    var catName = $(".cat-add-inp").val();
    $.post("/categories/add", {postId: postId, catName: catName}, () => {
            $(".cat-del:last-of-type").after(
                `<a class="cat-del">
                     <div class="changeable-cat chip">${catName}</div>
                     <div class="cat-del-ico">&times;</div>
                 </a>`
            );
        }
    );
});

//======= change language =========\\
$(".lang").on("click touch", function () {
    let searchParams = new URLSearchParams(window.location.search); // NOTE: IE is not supported
    var lang = $(this).attr("data-lang-name");
    searchParams.set("lang", lang);
    location.replace(`?${searchParams.toString()}`);
});

//======= change theme ========\\
$(".theme").on("click touch", function () {
    let searchParams = new URLSearchParams(window.location.search); // NOTE: IE is not supported
    var theme = $(this).attr("data-theme-name");
    searchParams.set("theme", theme);
    location.replace(`?${searchParams.toString()}`);
});

//================ SSE streams ================\\
$(document).ready(function () {
    var userName = $("nav .usr-name").text();
    if (userName.length === 0) return;

    var source = new EventSource(`/likes/${userName}/stream`);
    source.onmessage = event => {
        var likeEvent = JSON.parse(event.data);
        showPrompt(`One of your posts was ${likeEvent.message}`)
    };
});
