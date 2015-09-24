/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.persistence.view.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.testsuite.base.category.NoDatanucleus;
import com.blazebit.persistence.testsuite.base.category.NoEclipselink;
import com.blazebit.persistence.testsuite.base.category.NoOpenJPA;
import com.blazebit.persistence.view.AbstractEntityViewTest;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.basic.model.EmbeddableTestEntitySubView;
import com.blazebit.persistence.view.basic.model.EmbeddableTestEntityView;
import com.blazebit.persistence.view.basic.model.EmbeddableTestEntityViewWithSubview;
import com.blazebit.persistence.view.basic.model.IntIdEntityView;
import com.blazebit.persistence.view.entity.EmbeddableTestEntity;
import com.blazebit.persistence.view.entity.EmbeddableTestEntityId;
import com.blazebit.persistence.view.entity.IntIdEntity;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;

/**
 * This kind of mapping is not required to be supported by a JPA implementation.
 *
 * @author Christian Beikov
 * @since 1.0.6
 */
public class EmbeddableTestEntityViewTest extends AbstractEntityViewTest {

    protected static EntityViewManager evm;

    @Override
    protected Class<?>[] getEntityClasses() {
        return new Class<?>[]{
            EmbeddableTestEntity.class,
            IntIdEntity.class
        };
    }
    
    @BeforeClass
    public static void initEvm() {
        EntityViewConfiguration cfg = EntityViews.createDefaultConfiguration();
        cfg.addEntityView(IntIdEntityView.class);
        cfg.addEntityView(EmbeddableTestEntityView.class);
        cfg.addEntityView(EmbeddableTestEntityViewWithSubview.class);
        cfg.addEntityView(EmbeddableTestEntitySubView.class);
        evm = cfg.createEntityViewManager();
    }

    private EmbeddableTestEntity entity1;
    private EmbeddableTestEntity entity2;

    @Before
    public void setUp() {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            IntIdEntity intIdEntity1 = new IntIdEntity("1");
            entity1 = new EmbeddableTestEntity();
            entity1.setId(new EmbeddableTestEntityId(intIdEntity1, "1"));
            entity1.getEmbeddable().setManyToOne(null);
            entity1.getEmbeddable().getElementCollection().put("1", intIdEntity1);

            IntIdEntity intIdEntity2 = new IntIdEntity("2");
            entity2 = new EmbeddableTestEntity();
            entity2.setId(new EmbeddableTestEntityId(intIdEntity2, "2"));
            entity2.getEmbeddable().setManyToOne(entity1);
            entity2.getEmbeddable().getElementCollection().put("2", intIdEntity2);

            em.persist(intIdEntity1);
            em.persist(intIdEntity2);
            em.persist(entity1);
            em.persist(entity2);

            em.flush();
            tx.commit();
            em.clear();
            
            entity1 = cbf.create(em, EmbeddableTestEntity.class)
                .fetch("id.intIdEntity", "embeddable.manyToOne", "embeddable.oneToMany", "embeddable.elementCollection")
                .where("id").eq(entity1.getId())
                .getSingleResult();
            entity2 = cbf.create(em, EmbeddableTestEntity.class)
                .fetch("id.intIdEntity", "embeddable.manyToOne", "embeddable.oneToMany", "embeddable.elementCollection")
                .where("id").eq(entity2.getId())
                .getSingleResult();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Test
    @Category({NoDatanucleus.class, NoEclipselink.class, NoOpenJPA.class})
    public void testEmbeddableViewWithEntityRelations() {
        CriteriaBuilder<EmbeddableTestEntity> criteria = cbf.create(em, EmbeddableTestEntity.class, "e")
            .orderByAsc("id");
        CriteriaBuilder<EmbeddableTestEntityView> cb = evm.applySetting(EntityViewSetting.create(EmbeddableTestEntityView.class), criteria);
        List<EmbeddableTestEntityView> results = cb.getResultList();

        assertEquals(2, results.size());
        assertEqualViewEquals(entity1, results.get(0));
        assertEqualViewEquals(entity2, results.get(1));
    }

    @Test
    @Category({NoDatanucleus.class, NoEclipselink.class, NoOpenJPA.class})
    public void testEmbeddableViewWithSubViewRelations() {
        CriteriaBuilder<EmbeddableTestEntity> criteria = cbf.create(em, EmbeddableTestEntity.class, "e")
            .orderByAsc("id");
        CriteriaBuilder<EmbeddableTestEntityViewWithSubview> cb = evm.applySetting(EntityViewSetting.create(EmbeddableTestEntityViewWithSubview.class), criteria);
        List<EmbeddableTestEntityViewWithSubview> results = cb.getResultList();

        assertEquals(2, results.size());
        assertEqualViewEquals(entity1, results.get(0));
        assertEqualViewEquals(entity2, results.get(1));
    }
    
    private void assertEqualViewEquals(EmbeddableTestEntity entity, EmbeddableTestEntityView view) {
        assertEquals(entity.getId(), view.getId());
        assertEquals(entity.getId().getIntIdEntity(), view.getIdIntIdEntity());
        assertEquals(entity.getId().getIntIdEntity().getId(), view.getIdIntIdEntityId());
        assertEquals(entity.getId().getIntIdEntity().getName(), view.getIdIntIdEntityName());
        assertEquals(entity.getId().getKey(), view.getIdKey());
        assertEquals(entity.getEmbeddable().getManyToOne(), view.getEmbeddableManyToOne());
        assertEquals(entity.getEmbeddable().getOneToMany(), view.getEmbeddableOneToMany());
        assertEquals(entity.getEmbeddable().getElementCollection(), view.getEmbeddableElementCollection());
    }
    
    private void assertEqualViewEquals(EmbeddableTestEntity entity, EmbeddableTestEntityViewWithSubview view) {
        assertEquals(entity.getId(), view.getId());
        if (entity.getId().getIntIdEntity() == null) {
            assertNull(view.getIdIntIdEntity());
        } else {
            assertEquals(entity.getId().getIntIdEntity().getId(), view.getIdIntIdEntity().getId());
        }
        assertEquals(entity.getId().getIntIdEntity().getId(), view.getIdIntIdEntityId());
        assertEquals(entity.getId().getIntIdEntity().getName(), view.getIdIntIdEntityName());
        assertEquals(entity.getId().getKey(), view.getIdKey());
        if (entity.getEmbeddable().getManyToOne() == null) {
            assertNull(view.getEmbeddableManyToOneView());
        } else {
            assertEquals(entity.getEmbeddable().getManyToOne().getId(), view.getEmbeddableManyToOneView().getId());
        }
        
        assertEquals(entity.getEmbeddable().getOneToMany().size(), view.getEmbeddableOneToManyView().size());
        OUTER: for (EmbeddableTestEntity child : entity.getEmbeddable().getOneToMany()) {
            for (EmbeddableTestEntitySubView childView : view.getEmbeddableOneToManyView()) {
                if (child.getId().equals(childView.getId())) {
                    continue OUTER;
                }
            }
            
            fail("Couldn't find child view with id: " + child.getId());
        }
        assertEquals(entity.getEmbeddable().getElementCollection().size(), view.getEmbeddableElementCollectionView().size());
        for (Map.Entry<String, IntIdEntity> childEntry : entity.getEmbeddable().getElementCollection().entrySet()) {
            IntIdEntityView childView = view.getEmbeddableElementCollectionView().get(childEntry.getKey());
            assertEquals(childEntry.getValue().getId(), childView.getId());
        }
    }
}