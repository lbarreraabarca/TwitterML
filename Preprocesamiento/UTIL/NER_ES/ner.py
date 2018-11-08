from nltk import word_tokenize, pos_tag, ne_chunk
 
sentence = "webcamsdemexico alertachiapas vivio terremoto dentro plaza america tuxtla"
 
print ne_chunk(pos_tag(word_tokenize(sentence)))

sentence = "elhijofallado huracan irma caribe terremoto mexico peronismo argentina quien sufrio peor tragedia buen"
print ne_chunk(pos_tag(word_tokenize(sentence)))


sentence = "ante terremoto solo queda dar les consejo pega vuela esquiva"
print ne_chunk(pos_tag(word_tokenize(sentence)))
