(ns hello_noir.views.welcome
  (:require [hello_noir.views.common :as common]
            [noir.content.pages :as pages])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/" []
         (common/layout
           "welcome page"
           [:p "Temporary landing page."]))
