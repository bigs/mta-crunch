(ns hello_noir.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial layout [title & content]
            (html5
              [:head
               [:title title]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                content]]))
