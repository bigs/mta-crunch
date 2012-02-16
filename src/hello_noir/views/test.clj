(ns hello_noir.views.test
  (:require [hello_noir.views.common :as common]
            [noir.content.pages :as pages])
  (:use noir.core
        somnium.congomongo
        hiccup.core
        hiccup.page-helpers))

(defpage "/test" []
         (common/layout
           "area 51"
           [:p "Welcome to hello_noir experimental zone"]))

(defpage "/test/req/:name" {:keys [name]}
         (common/layout
           "request page"
           [:p (str "hello, " name)]))
