//================= Login modal =================\\
var closeBtn = $(".close");
var loginDialog = $("#login-dialog");
var loginWrapper = $("#login-wrapper");
var registerWrapper = $("#register-wrapper");

$("#log-in").click(function () {
    showModal(loginWrapper, registerWrapper);
});

$("#sign-up").click(function () {
    showModal(registerWrapper, loginWrapper);
});

function showModal(toShow, toHide) {
    toHide.hide();
    loginDialog.show();
    toShow.slideDown(350);
}

$(window).add(closeBtn).click(function (event) {
    if (event.target === loginDialog[0] || event.target === closeBtn[0]) {
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
    input.siblings(".highlight").hide();
    input.siblings(".highlight").css("background", "#00acc1");
    input.siblings(".prompt").hide();
}

$(".log-prompt").click(function () {
    $(".log-form>div").animate({height: "toggle", opacity: "toggle"}, 450);
});

//=========== Category page ajax load ===========\\
var page = 1;
var hasNext = true;
if ($(location).attr("pathname").match("^/posts/categories")) {
    var category = $(location).attr("pathname").replace("/posts/categories/", '');
    $(window).scroll(function () {
        if (hasNext && $(window).scrollTop() + $(window).height() > $(document).height() - 50) {
            $.post("/posts/category-ajax-load", {
                category: category,
                page: page
            }, function (slice) {
                if (slice === "") {
                    $("#spinner").hide();
                    hasNext = false;
                }
                $(".card:last-child").after(slice);
            });
            page++;
        }
    });
}

//========== Avatar preview on Sign up ==========\\
$(".avatar-upload input").on("change", function () {
    if (this.files && this.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $(".avatar-upload img").attr("src", e.target.result);
        };
        reader.readAsDataURL(this.files[0]);
    }
});

$(".post-file input").on("change", function () {
    if (this.files && this.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $(".file-prev").attr("src", e.target.result);
            $(".file-prev").attr("alt", "preview");
        };
        reader.readAsDataURL(this.files[0]);
    }
});

//==================== Like =====================\\
$("body").on("click touch", ".heart", function () {
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
    $.post("/posts/like", {postId: postId, like: isLike}, function (successful) {
        if (successful) {
            numElement.text(+numElement.text() + quantity);
        }
    });
}

$("body").on('animationend', ".heart", function () {
    $(this).toggleClass('not-liked liked');
});

//============== Side navigation ================\\
$(".hamburger-icon").click(function () {
    $(".side-nav").css("width", "50%");
});

$(".close-cat").click(function () {
    $(".side-nav").css("width", "0");
});

//================= Share icon ==================\\
$("body").on("click", ".share-icon", function () {
    var container = $(this).next();
    if (container.css("visibility") === "visible") {
        $(this).css({
            "background": "url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+CjxzdmcKICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICB4bWxuczpjYz0iaHR0cDovL2NyZWF0aXZlY29tbW9ucy5vcmcvbnMjIgogICB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiCiAgIHhtbG5zOnN2Zz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciCiAgIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIKICAgeG1sbnM6c29kaXBvZGk9Imh0dHA6Ly9zb2RpcG9kaS5zb3VyY2Vmb3JnZS5uZXQvRFREL3NvZGlwb2RpLTAuZHRkIgogICB4bWxuczppbmtzY2FwZT0iaHR0cDovL3d3dy5pbmtzY2FwZS5vcmcvbmFtZXNwYWNlcy9pbmtzY2FwZSIKICAgdmVyc2lvbj0iMS4xIgogICB4PSIwcHgiCiAgIHk9IjBweCIKICAgdmlld0JveD0iMCAwIDcwIDc2LjgwMDAwNSIKICAgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgMTAwIDEwMCIKICAgeG1sOnNwYWNlPSJwcmVzZXJ2ZSIKICAgaWQ9InN2ZzgiCiAgIHNvZGlwb2RpOmRvY25hbWU9ImljLXNoYXJlLnN2ZyIKICAgd2lkdGg9IjcwIgogICBoZWlnaHQ9Ijc2LjgwMDAwMyIKICAgaW5rc2NhcGU6dmVyc2lvbj0iMC45Mi4yICg1YzNlODBkLCAyMDE3LTA4LTA2KSI+PG1ldGFkYXRhCiAgICAgaWQ9Im1ldGFkYXRhMTQiPjxyZGY6UkRGPjxjYzpXb3JrCiAgICAgICAgIHJkZjphYm91dD0iIj48ZGM6Zm9ybWF0PmltYWdlL3N2Zyt4bWw8L2RjOmZvcm1hdD48ZGM6dHlwZQogICAgICAgICAgIHJkZjpyZXNvdXJjZT0iaHR0cDovL3B1cmwub3JnL2RjL2RjbWl0eXBlL1N0aWxsSW1hZ2UiIC8+PGRjOnRpdGxlPjwvZGM6dGl0bGU+PC9jYzpXb3JrPjwvcmRmOlJERj48L21ldGFkYXRhPjxkZWZzCiAgICAgaWQ9ImRlZnMxMiIgLz48c29kaXBvZGk6bmFtZWR2aWV3CiAgICAgcGFnZWNvbG9yPSIjZmZmZmZmIgogICAgIGJvcmRlcmNvbG9yPSIjNjY2NjY2IgogICAgIGJvcmRlcm9wYWNpdHk9IjEiCiAgICAgb2JqZWN0dG9sZXJhbmNlPSIxMCIKICAgICBncmlkdG9sZXJhbmNlPSIxMCIKICAgICBndWlkZXRvbGVyYW5jZT0iMTAiCiAgICAgaW5rc2NhcGU6cGFnZW9wYWNpdHk9IjAiCiAgICAgaW5rc2NhcGU6cGFnZXNoYWRvdz0iMiIKICAgICBpbmtzY2FwZTp3aW5kb3ctd2lkdGg9IjEzNjYiCiAgICAgaW5rc2NhcGU6d2luZG93LWhlaWdodD0iNzA1IgogICAgIGlkPSJuYW1lZHZpZXcxMCIKICAgICBzaG93Z3JpZD0iZmFsc2UiCiAgICAgZml0LW1hcmdpbi10b3A9IjAiCiAgICAgZml0LW1hcmdpbi1sZWZ0PSIwIgogICAgIGZpdC1tYXJnaW4tcmlnaHQ9IjAiCiAgICAgZml0LW1hcmdpbi1ib3R0b209IjAiCiAgICAgaW5rc2NhcGU6em9vbT0iMS4zMzUwMTc2IgogICAgIGlua3NjYXBlOmN4PSItMTUxLjM4MTI2IgogICAgIGlua3NjYXBlOmN5PSItMTMuNDA5OTYiCiAgICAgaW5rc2NhcGU6d2luZG93LXg9Ii04IgogICAgIGlua3NjYXBlOndpbmRvdy15PSItOCIKICAgICBpbmtzY2FwZTp3aW5kb3ctbWF4aW1pemVkPSIxIgogICAgIGlua3NjYXBlOmN1cnJlbnQtbGF5ZXI9InN2ZzgiCiAgICAgaW5rc2NhcGU6bWVhc3VyZS1zdGFydD0iLTEuNTY4MzMsMzcuNDA1OSIKICAgICBpbmtzY2FwZTptZWFzdXJlLWVuZD0iLTAuMDIzNDA3OSwzOC4wODQ3IgogICAgIHNjYWxlLXg9IjEiIC8+PHBhdGgKICAgICBkPSJtIDU3LjEsMjUuODAwMDA0IGMgNy4xLDAgMTIuOSwtNS44IDEyLjksLTEyLjkgQyA3MCw1Ljc5OTk5NyA2NC4yLDAgNTcuMSwwIDUwLDAgNDQuMiw1Ljc5OTk5NyA0NC4yLDEyLjkwMDAwNCBjIDAsMS40IDAuMiwyLjcgMC42LDMuOSBsIC0yMi4zLDEyLjkgYyAtMi40LC0yLjYgLTUuOCwtNC4yIC05LjUsLTQuMiAtNy4yLDAgLTEzLDUuOCAtMTMsMTIuOSAwLDcuMSA1LjgsMTIuOSAxMi45LDEyLjkgMy44LDAgNy4yLC0xLjYgOS41LC00LjIgbCAyMi4zLDEyLjkgYyAtMC40LDEuMiAtMC42LDIuNiAtMC42LDMuOSAwLDcuMSA1LjgsMTIuOSAxMi45LDEyLjkgNy4xLDAgMTIuOSwtNS44IDEyLjksLTEyLjkgMCwtNy4xIC01LjgsLTEyLjkgLTEyLjksLTEyLjkgLTMuOCwwIC03LjIsMS42IC05LjUsNC4yIGwgLTIyLjMsLTEyLjkgYyAwLjQsLTEuMiAwLjYsLTIuNiAwLjYsLTMuOSAwLC0xLjQgLTAuMiwtMi43IC0wLjYsLTMuOSBsIDIyLjMsLTEyLjkgYyAyLjQsMi42IDUuOCw0LjIgOS42LDQuMiB6IgogICAgIGlkPSJwYXRoMiIKICAgICBpbmtzY2FwZTpjb25uZWN0b3ItY3VydmF0dXJlPSIwIgogICAgIHN0eWxlPSJmaWxsOiNhYWI4YzI7ZmlsbC1vcGFjaXR5OjEiIC8+PC9zdmc+) no-repeat",
            "background-size": "13px"
        });
        container.css({
            "opacity": "0",
            "visibility": "hidden",
            "transform": "translate(-20px, 0)"
        });
    } else {
        $(this).css({
            "background": "url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+CjxzdmcKICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICB4bWxuczpjYz0iaHR0cDovL2NyZWF0aXZlY29tbW9ucy5vcmcvbnMjIgogICB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiCiAgIHhtbG5zOnN2Zz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciCiAgIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIKICAgeG1sbnM6c29kaXBvZGk9Imh0dHA6Ly9zb2RpcG9kaS5zb3VyY2Vmb3JnZS5uZXQvRFREL3NvZGlwb2RpLTAuZHRkIgogICB4bWxuczppbmtzY2FwZT0iaHR0cDovL3d3dy5pbmtzY2FwZS5vcmcvbmFtZXNwYWNlcy9pbmtzY2FwZSIKICAgdmVyc2lvbj0iMS4xIgogICB4PSIwcHgiCiAgIHk9IjBweCIKICAgdmlld0JveD0iMCAwIDcwIDc2LjgwMDAxMyIKICAgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgMTAwIDEwMCIKICAgeG1sOnNwYWNlPSJwcmVzZXJ2ZSIKICAgaWQ9InN2ZzgiCiAgIHNvZGlwb2RpOmRvY25hbWU9ImljLXNoYXJlLTIuc3ZnIgogICB3aWR0aD0iNzAiCiAgIGhlaWdodD0iNzYuODAwMDExIgogICBpbmtzY2FwZTp2ZXJzaW9uPSIwLjkyLjIgKDVjM2U4MGQsIDIwMTctMDgtMDYpIj48bWV0YWRhdGEKICAgICBpZD0ibWV0YWRhdGExNCI+PHJkZjpSREY+PGNjOldvcmsKICAgICAgICAgcmRmOmFib3V0PSIiPjxkYzpmb3JtYXQ+aW1hZ2Uvc3ZnK3htbDwvZGM6Zm9ybWF0PjxkYzp0eXBlCiAgICAgICAgICAgcmRmOnJlc291cmNlPSJodHRwOi8vcHVybC5vcmcvZGMvZGNtaXR5cGUvU3RpbGxJbWFnZSIgLz48ZGM6dGl0bGU+PC9kYzp0aXRsZT48L2NjOldvcms+PC9yZGY6UkRGPjwvbWV0YWRhdGE+PGRlZnMKICAgICBpZD0iZGVmczEyIiAvPjxzb2RpcG9kaTpuYW1lZHZpZXcKICAgICBwYWdlY29sb3I9IiNmZmZmZmYiCiAgICAgYm9yZGVyY29sb3I9IiM2NjY2NjYiCiAgICAgYm9yZGVyb3BhY2l0eT0iMSIKICAgICBvYmplY3R0b2xlcmFuY2U9IjEwIgogICAgIGdyaWR0b2xlcmFuY2U9IjEwIgogICAgIGd1aWRldG9sZXJhbmNlPSIxMCIKICAgICBpbmtzY2FwZTpwYWdlb3BhY2l0eT0iMCIKICAgICBpbmtzY2FwZTpwYWdlc2hhZG93PSIyIgogICAgIGlua3NjYXBlOndpbmRvdy13aWR0aD0iMTM2NiIKICAgICBpbmtzY2FwZTp3aW5kb3ctaGVpZ2h0PSI3MDUiCiAgICAgaWQ9Im5hbWVkdmlldzEwIgogICAgIHNob3dncmlkPSJmYWxzZSIKICAgICBmaXQtbWFyZ2luLXRvcD0iMCIKICAgICBmaXQtbWFyZ2luLWxlZnQ9IjAiCiAgICAgZml0LW1hcmdpbi1yaWdodD0iMCIKICAgICBmaXQtbWFyZ2luLWJvdHRvbT0iMCIKICAgICBpbmtzY2FwZTp6b29tPSIxLjMzNTAxNzYiCiAgICAgaW5rc2NhcGU6Y3g9IjYzLjU5NzE4NCIKICAgICBpbmtzY2FwZTpjeT0iMy44MTI1OTQ1IgogICAgIGlua3NjYXBlOndpbmRvdy14PSItOCIKICAgICBpbmtzY2FwZTp3aW5kb3cteT0iLTgiCiAgICAgaW5rc2NhcGU6d2luZG93LW1heGltaXplZD0iMSIKICAgICBpbmtzY2FwZTpjdXJyZW50LWxheWVyPSJzdmc4IgogICAgIGlua3NjYXBlOm1lYXN1cmUtc3RhcnQ9Ii0xLjU2ODMzLDM3LjQwNTkiCiAgICAgaW5rc2NhcGU6bWVhc3VyZS1lbmQ9Ii0wLjAyMzQwNzksMzguMDg0NyIKICAgICBzY2FsZS14PSIxIiAvPjxwYXRoCiAgICAgc3R5bGU9ImZpbGw6IzAwYWNjMTtmaWxsLW9wYWNpdHk6MSIKICAgICBpbmtzY2FwZTpjb25uZWN0b3ItY3VydmF0dXJlPSIwIgogICAgIGlkPSJwYXRoMi04IgogICAgIGQ9Ik0gNTcuMSwyNS44IEMgNjQuMiwyNS44IDcwLDIwIDcwLDEyLjkgNzAsNS44IDY0LjIsMCA1Ny4xLDAgNTAsMCA0NC4yLDUuOCA0NC4yLDEyLjkgYyAwLDEuNCAwLjIsMi43IDAuNiwzLjkgTCAyMi41LDI5LjcgQyAyMC4xLDI3LjEgMTYuNywyNS41IDEzLDI1LjUgNS44LDI1LjUgMCwzMS4zIDAsMzguNDAwMDEgYyAwLDcuMSA1LjgsMTIuOSAxMi45LDEyLjkgMy44LDAgNy4yLC0xLjYgOS41LC00LjIgbCAyMi4zLDEyLjkgYyAtMC40LDEuMiAtMC42LDIuNiAtMC42LDMuOSAwLDcuMSA1LjgsMTIuOSAxMi45LDEyLjkgNy4xLDAgMTIuOSwtNS44IDEyLjksLTEyLjkgMCwtNy4xIC01LjgsLTEyLjkgLTEyLjksLTEyLjkgLTMuOCwwIC03LjIsMS42IC05LjUsNC4yIGwgLTIyLjMsLTEyLjkgYyAwLjQsLTEuMiAwLjYsLTIuNiAwLjYsLTMuOSBDIDI1LjgsMzcgMjUuNiwzNS43IDI1LjIsMzQuNSBMIDQ3LjUsMjEuNiBjIDIuNCwyLjYgNS44LDQuMiA5LjYsNC4yIHoiIC8+PC9zdmc+) no-repeat",
            "background-size": "13px"
        });
        container.css({
            "opacity": "1",
            "visibility": "visible",
            "transform": "translate(-20px, 10px)"
        });
    }
});

$(window).click(function (ev) {
    if (ev.target.className !== "share-icon") {
        $(".share-content").each(function () {
            if ($(this).is(":visible")) {
                $(this).siblings(".share-icon").css({
                    "background": "url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+CjxzdmcKICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICB4bWxuczpjYz0iaHR0cDovL2NyZWF0aXZlY29tbW9ucy5vcmcvbnMjIgogICB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiCiAgIHhtbG5zOnN2Zz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciCiAgIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIKICAgeG1sbnM6c29kaXBvZGk9Imh0dHA6Ly9zb2RpcG9kaS5zb3VyY2Vmb3JnZS5uZXQvRFREL3NvZGlwb2RpLTAuZHRkIgogICB4bWxuczppbmtzY2FwZT0iaHR0cDovL3d3dy5pbmtzY2FwZS5vcmcvbmFtZXNwYWNlcy9pbmtzY2FwZSIKICAgdmVyc2lvbj0iMS4xIgogICB4PSIwcHgiCiAgIHk9IjBweCIKICAgdmlld0JveD0iMCAwIDcwIDc2LjgwMDAwNSIKICAgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgMTAwIDEwMCIKICAgeG1sOnNwYWNlPSJwcmVzZXJ2ZSIKICAgaWQ9InN2ZzgiCiAgIHNvZGlwb2RpOmRvY25hbWU9ImljLXNoYXJlLnN2ZyIKICAgd2lkdGg9IjcwIgogICBoZWlnaHQ9Ijc2LjgwMDAwMyIKICAgaW5rc2NhcGU6dmVyc2lvbj0iMC45Mi4yICg1YzNlODBkLCAyMDE3LTA4LTA2KSI+PG1ldGFkYXRhCiAgICAgaWQ9Im1ldGFkYXRhMTQiPjxyZGY6UkRGPjxjYzpXb3JrCiAgICAgICAgIHJkZjphYm91dD0iIj48ZGM6Zm9ybWF0PmltYWdlL3N2Zyt4bWw8L2RjOmZvcm1hdD48ZGM6dHlwZQogICAgICAgICAgIHJkZjpyZXNvdXJjZT0iaHR0cDovL3B1cmwub3JnL2RjL2RjbWl0eXBlL1N0aWxsSW1hZ2UiIC8+PGRjOnRpdGxlPjwvZGM6dGl0bGU+PC9jYzpXb3JrPjwvcmRmOlJERj48L21ldGFkYXRhPjxkZWZzCiAgICAgaWQ9ImRlZnMxMiIgLz48c29kaXBvZGk6bmFtZWR2aWV3CiAgICAgcGFnZWNvbG9yPSIjZmZmZmZmIgogICAgIGJvcmRlcmNvbG9yPSIjNjY2NjY2IgogICAgIGJvcmRlcm9wYWNpdHk9IjEiCiAgICAgb2JqZWN0dG9sZXJhbmNlPSIxMCIKICAgICBncmlkdG9sZXJhbmNlPSIxMCIKICAgICBndWlkZXRvbGVyYW5jZT0iMTAiCiAgICAgaW5rc2NhcGU6cGFnZW9wYWNpdHk9IjAiCiAgICAgaW5rc2NhcGU6cGFnZXNoYWRvdz0iMiIKICAgICBpbmtzY2FwZTp3aW5kb3ctd2lkdGg9IjEzNjYiCiAgICAgaW5rc2NhcGU6d2luZG93LWhlaWdodD0iNzA1IgogICAgIGlkPSJuYW1lZHZpZXcxMCIKICAgICBzaG93Z3JpZD0iZmFsc2UiCiAgICAgZml0LW1hcmdpbi10b3A9IjAiCiAgICAgZml0LW1hcmdpbi1sZWZ0PSIwIgogICAgIGZpdC1tYXJnaW4tcmlnaHQ9IjAiCiAgICAgZml0LW1hcmdpbi1ib3R0b209IjAiCiAgICAgaW5rc2NhcGU6em9vbT0iMS4zMzUwMTc2IgogICAgIGlua3NjYXBlOmN4PSItMTUxLjM4MTI2IgogICAgIGlua3NjYXBlOmN5PSItMTMuNDA5OTYiCiAgICAgaW5rc2NhcGU6d2luZG93LXg9Ii04IgogICAgIGlua3NjYXBlOndpbmRvdy15PSItOCIKICAgICBpbmtzY2FwZTp3aW5kb3ctbWF4aW1pemVkPSIxIgogICAgIGlua3NjYXBlOmN1cnJlbnQtbGF5ZXI9InN2ZzgiCiAgICAgaW5rc2NhcGU6bWVhc3VyZS1zdGFydD0iLTEuNTY4MzMsMzcuNDA1OSIKICAgICBpbmtzY2FwZTptZWFzdXJlLWVuZD0iLTAuMDIzNDA3OSwzOC4wODQ3IgogICAgIHNjYWxlLXg9IjEiIC8+PHBhdGgKICAgICBkPSJtIDU3LjEsMjUuODAwMDA0IGMgNy4xLDAgMTIuOSwtNS44IDEyLjksLTEyLjkgQyA3MCw1Ljc5OTk5NyA2NC4yLDAgNTcuMSwwIDUwLDAgNDQuMiw1Ljc5OTk5NyA0NC4yLDEyLjkwMDAwNCBjIDAsMS40IDAuMiwyLjcgMC42LDMuOSBsIC0yMi4zLDEyLjkgYyAtMi40LC0yLjYgLTUuOCwtNC4yIC05LjUsLTQuMiAtNy4yLDAgLTEzLDUuOCAtMTMsMTIuOSAwLDcuMSA1LjgsMTIuOSAxMi45LDEyLjkgMy44LDAgNy4yLC0xLjYgOS41LC00LjIgbCAyMi4zLDEyLjkgYyAtMC40LDEuMiAtMC42LDIuNiAtMC42LDMuOSAwLDcuMSA1LjgsMTIuOSAxMi45LDEyLjkgNy4xLDAgMTIuOSwtNS44IDEyLjksLTEyLjkgMCwtNy4xIC01LjgsLTEyLjkgLTEyLjksLTEyLjkgLTMuOCwwIC03LjIsMS42IC05LjUsNC4yIGwgLTIyLjMsLTEyLjkgYyAwLjQsLTEuMiAwLjYsLTIuNiAwLjYsLTMuOSAwLC0xLjQgLTAuMiwtMi43IC0wLjYsLTMuOSBsIDIyLjMsLTEyLjkgYyAyLjQsMi42IDUuOCw0LjIgOS42LDQuMiB6IgogICAgIGlkPSJwYXRoMiIKICAgICBpbmtzY2FwZTpjb25uZWN0b3ItY3VydmF0dXJlPSIwIgogICAgIHN0eWxlPSJmaWxsOiNhYWI4YzI7ZmlsbC1vcGFjaXR5OjEiIC8+PC9zdmc+) no-repeat",
                    "background-size": "13px"
                });
                $(this).css({
                    "opacity": "0",
                    "visibility": "hidden",
                    "transform": "translate(-20px, 0)"
                });
            }
        });
    }
});

//============== Check existing userName ==================\\
var usernameInput = $(".register-form .input:nth-child(1) input");
var passwordInput = $(".register-form .input:nth-child(2) input");
var passRegex = /(\d\w*[a-zA-Z]\w*)|([a-zA-Z]\w*\d\w*)/;

$(".flat-inp").focus(function () {
    var content = $(this).val();
    if (content === "") {
        $(this).siblings(".highlight").fadeTo(180, 1);
    }
});

function checkUserName(name, highlight) {
    $.post("/users/check-username", {name: name}, function (exists) {
        if (exists) {
            highlight.css("background", "#de1e26");
            highlight.fadeTo(200, 1);
            usernameInput.siblings(".prompt").text("Already taken");
            usernameInput.siblings(".prompt").fadeTo(200, 1);
        } else {
            highlight.css("background", "#4CAF50");
            highlight.fadeTo(200, 1);
            usernameInput.siblings(".prompt").fadeTo(200, 0);
        }
    });
}

usernameInput.blur(function () {
    var name = usernameInput.val();
    var highlight = usernameInput.siblings(".highlight");
    if (name === "") {
        highlight.fadeTo(200, 0, function () {
            highlight.css("background", "#00acc1");
        });
        usernameInput.siblings(".prompt").fadeTo(200, 0);
    } else {
        checkUserName(name, highlight);
    }
});

passwordInput.blur(function () {
    var pass = passwordInput.val();
    var highlight = passwordInput.siblings(".highlight");
    if (pass.length === 0) {
        highlight.fadeTo(180, 0, function () {
            highlight.css("background", "#00acc1");
        });
        passwordInput.siblings(".prompt").fadeTo(200, 0);
    } else if (pass.length < 6 || !passRegex.test(pass)) {
        highlight.fadeTo(200, 1);
        highlight.css("background", "#de1e26");
        if (pass.length < 6) {
            passwordInput.siblings(".prompt").text("Too short");
        } else {
            passwordInput.siblings(".prompt").text("Invalid");
        }
        passwordInput.siblings(".prompt").fadeTo(200, 1);
    } else {
        highlight.fadeTo(200, 1);
        highlight.css("background", "#4CAF50");
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
        nameHighlight.css("background", "#de1e26");
        nameHighlight.fadeTo(200, 1);
        usernameInput.siblings(".prompt").text("Required");
        usernameInput.siblings(".prompt").fadeTo(200, 1);
        submit = false;
    }
    if (pass.length === 0) {
        passHighlight.css("background", "#de1e26");
        passHighlight.fadeTo(200, 1);
        passwordInput.siblings(".prompt").text("Required");
        passwordInput.siblings(".prompt").fadeTo(200, 1);
        submit = false;
    } else if (!passRegex.test(pass)) {
        passHighlight.css("background", "#de1e26");
        passHighlight.fadeTo(200, 1);
        passwordInput.siblings(".prompt").text("Invalid");
        passwordInput.siblings(".prompt").fadeTo(200, 1);
        submit = false;
    }
    return submit;
});

//======================== AJAX login ======================
var usernameInputLogin = $(".login-form .input:nth-child(1) input");
var passwordInputLogin = $(".login-form .input:nth-child(2) input");

usernameInputLogin.blur(function () {
    var name = usernameInputLogin.val();
    var highlight = usernameInputLogin.siblings(".highlight");
    if (name.length === 0) {
        highlight.fadeTo(200, 0, function () {
            highlight.css("background", "#00acc1");
        });
        usernameInputLogin.siblings(".prompt").fadeTo(200, 0);
    }
});

passwordInputLogin.blur(function () {
    var pass = passwordInputLogin.val();
    var highlight = passwordInputLogin.siblings(".highlight");
    if (pass.length === 0) {
        highlight.fadeTo(200, 0, function () {
            highlight.css("background", "#00acc1");
        });
        passwordInputLogin.siblings(".prompt").fadeTo(200, 0);
    }
});

$(".login-form").submit(function () {
    var submit = true;
    var name = usernameInputLogin.val();
    var pass = passwordInputLogin.val();
    var nameHighlight = usernameInputLogin.siblings(".highlight");
    var passHighlight = passwordInputLogin.siblings(".highlight");
    if (name.length === 0) {
        nameHighlight.css("background", "#de1e26");
        nameHighlight.fadeTo(200, 1);
        usernameInputLogin.siblings(".prompt").text("Required");
        usernameInputLogin.siblings(".prompt").fadeTo(200, 1);
        submit = false;
    }
    if (pass.length === 0) {
        passHighlight.css("background", "#de1e26");
        passHighlight.fadeTo(200, 1);
        passwordInputLogin.siblings(".prompt").text("Required");
        passwordInputLogin.siblings(".prompt").fadeTo(200, 1);
        submit = false;
    }
    return submit;
});

