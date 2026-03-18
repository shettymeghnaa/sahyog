//const BASE_URL = "http://localhost:8080";
//
//function login() {
//
//    const username = document.getElementById("username").value;
//    const password = document.getElementById("password").value;
//
//    fetch("/api/auth/login", {
//        method: "POST",
//        headers: {
//            "Content-Type": "application/json"
//        },
//        body: JSON.stringify({
//            username: username,
//            password: password
//        })
//    })
//        .then(res => {
//            console.log("Status:", res.status);
//            return res.text().then(text => {
//                return { status: res.status, body: text };
//            });
//        })
//        .then(data => {
//
//            console.log("Response Body:", data.body);
//
//            if (data.status === 200) {
//
//                const json = JSON.parse(data.body);
//                localStorage.setItem("token", json.token);
//
//                const payload = JSON.parse(atob(json.token.split('.')[1]));
//                const role = payload.role;
//
//                if (role === "ROLE_ADMIN") {
//                    window.location.href = "/dashboard.html";
//                } else {
//                    window.location.href = "/dashboard.html";
//                }
//
//            } else {
//                document.getElementById("error").innerText =
//                    "Login failed: " + data.body;
//            }
//
//        })
//        .catch(error => {
//            console.error(error);
//            document.getElementById("error").innerText = "Server error";
//        });
//}
//
//function getRegions() {
//
//    const token = localStorage.getItem("token");
//
//    fetch(BASE_URL + "/api/regions", {
//        headers: {
//            "Authorization": "Bearer " + token
//        }
//    })
//        .then(res => res.json())
//        .then(data => {
//
//            const list = document.getElementById("regionsList");
//            list.innerHTML = "";
//
//            data.forEach(region => {
//                const li = document.createElement("li");
//                li.innerText = region.name;
//                list.appendChild(li);
//            });
//        });
//}
//
//function viewMyClaims() {
//
//    const token = localStorage.getItem("token");
//    const payload = JSON.parse(atob(token.split('.')[1]));
//    const memberId = payload.userId;
//
//    fetch(BASE_URL + "/api/claims/member/" + memberId, {
//        headers: {
//            "Authorization": "Bearer " + token
//        }
//    })
//        .then(res => res.json())
//        .then(data => {
//
//            const list = document.getElementById("claimsList");
//            list.innerHTML = "";
//
//            data.forEach(claim => {
//                const li = document.createElement("li");
//                li.innerText = "Claim ID: " + claim.id +
//                    " | Status: " + claim.status;
//                list.appendChild(li);
//            });
//        });
//}