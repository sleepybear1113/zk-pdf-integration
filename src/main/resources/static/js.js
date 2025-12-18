/**
 *     private String filename;
 *     private String filenameOriginal;
 *     private Integer originalPageCount;
 *     private Integer newPageCount;
 *
 *     private String outputPdfPath;
 */
class PdfResultInfoDto {
    constructor(props = {}) {
        this.filename = props.filename;
        this.filenameOriginal = props.filenameOriginal;
        this.originalPageCount = props.originalPageCount;
        this.newPageCount = props.newPageCount;
        this.outputPdfPath = props.outputPdfPath;
        this.errorList = props.errorList ? props.errorList.map(item => String(item)) : [];
    }
}

/**
 * 点击按钮，在 id = pdf-upload-div 中清空所有元素，然后添加一个 input[type=file] 元素并点击，只接受 PDF
 */
function selectFile() {
    let n = document.getElementById('n-input').value;
    if (!n || n < 1 || n > 5) {
        alert("重复次数请输入 1-5 之间的整数");
        return false;
    }

    let pdfUploadDiv = document.getElementById("pdf-upload-div");
    let filenameInput = document.getElementById("filename-input");
    pdfUploadDiv.innerHTML = "";
    filenameInput.value = "";
    let input = document.createElement("input");
    input.type = "file";
    input.accept = ".pdf";
    input.onchange = function () {
        let file = input.files[0];
        if (file) {
            filenameInput.value = file.name;

            document.getElementById("select-file-button").disabled = true;
            document.getElementById("pdf-upload-info").innerHTML = "";
            document.getElementById("pdf-result-info-div").innerHTML = "";

            let picFile = document.getElementById("pic-input-hide")?.files?.[0];
            uploadPdf(file, picFile);
        }
    };
    pdfUploadDiv.appendChild(input);
    input.click();
}

function selectPicFile() {
    let picUploadDiv = document.getElementById("pic-upload-div");
    let filenameInput = document.getElementById("pic-input");
    picUploadDiv.innerHTML = "";
    filenameInput.value = "";
    let input = document.createElement("input");
    input.type = "file";
    input.id = "pic-input-hide";
    input.accept = ".jpg,.jpeg,.png,.gif,.bmp";
    input.onchange = function () {
        let file = input.files[0];
        if (file) {
            filenameInput.value = file.name;
        }
    };
    picUploadDiv.appendChild(input);
    input.click();
}

/**
 * 上传 PDF 文件
 * @param file
 * @param picFile
 */
function uploadPdf(file, picFile) {
    let formData = new FormData();
    formData.append("files", file);
    formData.append("files", picFile);
    axios.post("/upload/file", formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
        onUploadProgress: (progressEvent) => {
            let percent = ((progressEvent.loaded * 100) / progressEvent.total).toFixed(2);
            document.getElementById("pdf-upload-info").innerHTML = `<p>上传进度：${percent}%</p>`;
            if (percent >= 100) {
                document.getElementById("pdf-upload-info").innerHTML += `<p>上传完成！</p><p id="process-p"></p>`;
            }
        }
    }).then(response => {
        let data = response.data;
        let code = data.code;
        if (code === 0) {
            let res = data.result;
            if (!res) {
                return;
            }

            let files = res ? res.map(item => item) : [];
            console.log(files);
            integrationPdf(files[0], files.length > 1 ? files[1] : null);
        }
    }).catch(error => {
        document.getElementById("select-file-button").disabled = false;
    });
}

function integrationPdf(filename, picFilename) {
    let n = document.getElementById('n-input').value;
    if (!n || n < 1 || n > 5) {
        alert("重复次数请输入 1-5 之间的整数");
        return false;
    }

    let sort = !!document.getElementById("sort-1").checked;

    document.getElementById("process-p").innerHTML = "处理中";
    let processing = setInterval(() => {
        if (document.getElementById("process-p").innerHTML.length > 10) {
            document.getElementById("process-p").innerHTML = "处理中";
        }
        document.getElementById("process-p").innerHTML += ".";
    }, 300);

    axios.get("/integration/pdf", {
        params: {
            filename: filename,
            n: n,
            sort: sort,
            picFilename: picFilename,
        }
    }).then(response => {
        clearInterval(processing);
        let data = response.data;
        let code = data.code;
        if (code === 0) {
            let res = data.result;
            if (!res) {
                return;
            }

            let pdfResultInfoDto = new PdfResultInfoDto(res);
            console.log(pdfResultInfoDto);
            let pdfResultInfoDiv = document.getElementById("pdf-result-info-div");
            pdfResultInfoDiv.innerHTML = "";
            let div = document.createElement("div");
            div.innerHTML = `
                <p>处理结束！</p>
                <p>原页数：${pdfResultInfoDto.originalPageCount}，新页数：${pdfResultInfoDto.newPageCount}。</p>
            `;
            if (pdfResultInfoDto.newPageCount > 0) {
                div.innerHTML += `<p><a target="_blank" href=${axios.defaults.baseURL}/${pdfResultInfoDto.outputPdfPath}>点击下载</a></p>`;
            }
            if (pdfResultInfoDto.errorList.length > 0) {
                div.innerHTML += "<p>错误信息：</p>";
                let ul = document.createElement("ul");
                pdfResultInfoDto.errorList.forEach(item => {
                    let li = document.createElement("li");
                    li.innerText = item;
                    ul.appendChild(li);
                });
                div.appendChild(ul);
            }
            pdfResultInfoDiv.appendChild(div);
            document.getElementById("select-file-button").disabled = false;
        }
    }).catch(error => {
        clearInterval(processing);
        document.getElementById("select-file-button").disabled = false;
    });
}