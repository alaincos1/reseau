package fr.ul.miage.reseau.html;

import fr.ul.miage.reseau.enumutils.HttpStatus;
import j2html.tags.ContainerTag;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class HtmlGenerator {

    public static String renderExceptionHtml(String exceptionMessage, HttpStatus httpStatus) {
        String srcImg;
        switch(httpStatus.getValue()){
            case 400:
                srcImg = "https://www.elegantthemes.com/blog/wp-content/uploads/2020/07/000-HTTP-Error-400.png";
                break;
            case 403:
                srcImg = "http://www.iafactory.fr/img/visuel/403-error-iafactory.png";
                break;
            case 404:
                srcImg = "https://www.fabricegueroux.com/photo/art/default/7787257-12066922.jpg?v=1431546251";
                break;
            default:
                srcImg = "https://www.fabricegueroux.com/photo/art/default/7787257-12066922.jpg?v=1431546251";
        }
        ContainerTag html =
                html().attr("lang", "fr")
                        .with(head().with(
                                        meta().withCharset("utf-8"),
                                        title("Exception occured"),
                                body().with(
                                        header().with(h1(exceptionMessage)),
                                        div()
                                                .with(
                                                        img().withSrc(srcImg)
                                                ))));
        return document().render() + html.render();
    }

    public static String renderPathFinderHtml(List<File> files, String folderName) {
        ContainerTag html =
                html().attr("lang", "fr")
                        .with(head().with(
                                meta().withCharset("utf-8"),
                                title("Repository Menu"),
                                body().with(
                                        header().with(h1("Repository Menu")),
                                        div()
                                                .with(
                                                        ul().with(files.stream().map((file) ->
                                                            li(a(file.getName()).withHref("/" + folderName +"/"+file.getName())))
                                                        .collect(Collectors.toList()))))));

        return document().render() + html.render();
    }

}
