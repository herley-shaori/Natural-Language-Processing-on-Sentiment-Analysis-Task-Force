import numpy as np

from gensim import utils
from gensim.models.doc2vec import TaggedDocument
from gensim.models import Doc2Vec
import random
import numpy

class TaggedLineSentence(object):
    def __init__(self, sources):
        self.sources = sources

        flipped = {}

        # make sure that keys are unique
        for key, value in sources.items():
            if value not in flipped:
                flipped[value] = [key]
            else:
                raise Exception('Non-unique prefix encountered')

    def __iter__(self):
        for source, prefix in self.sources.items():
            with utils.smart_open(source) as fin:
                for item_no, line in enumerate(fin):
                    yield TaggedDocument(utils.to_unicode(line).split(), [prefix + '_%s' % item_no])

    def to_array(self):
        self.sentences = []
        for source, prefix in self.sources.items():
            with utils.smart_open(source) as fin:
                for item_no, line in enumerate(fin):
                    # print("Prefix: ", prefix, " Number: ",item_no)
                    self.sentences.append(TaggedDocument(utils.to_unicode(line,encoding='ISO-8859-1').split(), [prefix + '_%s' % item_no]))
        return(self.sentences)

    def sentences_perm(self):
        shuffled = list(self.sentences)
        random.shuffle(shuffled)
        return(shuffled)

# log.info('source load')
# sources = {'food-POS.txt':'TRAIN_FOOD_POS', 'food-NEG.txt':'TRAIN_FOOD_NEG', 'food-UNK.txt':'TRAIN_FOOD_UNK'}
# sources = {'price-POS.txt':'TRAIN_PRICE_POS', 'price-NEG.txt':'TRAIN_PRICE_NEG', 'price-UNK.txt':'TRAIN_PRICE_UNK'}
# sources = {'service-POS.txt':'TRAIN_SERVICE_POS', 'service-NEG.txt':'TRAIN_SERVICE_NEG', 'service-UNK.txt':'TRAIN_SERVICE_UNK'}
sources = {'ambience-POS.txt':'TRAIN_AMBIENCE_POS', 'ambience-NEG.txt':'TRAIN_AMBIENCE_NEG', 'ambience-UNK.txt':'TRAIN_AMBIENCE_UNK'}

# log.info('TaggedDocument')
sentences = TaggedLineSentence(sources)

# log.info('D2V')
# model = Doc2Vec(min_count=1, window=10, vector_size=300, sample=1e-4, negative=5, workers=1)
model = Doc2Vec(min_count=1, window=5, vector_size=300, sample=1e-4, workers=1)
model.build_vocab(sentences.to_array())
sentences.sentences_perm()

# # # DV Training - do not modify
model.train(sentences.sentences_perm(),total_examples=model.corpus_count,epochs=100)

print("Model Similar with Food: ", model.most_similar('food'))

# log.info('Model Save')
# model.save('foodAspect.d2v')
# model.save('priceAspect.d2v')
# model.save('serviceAspect.d2v')
model.save('ambienceAspect.d2v')